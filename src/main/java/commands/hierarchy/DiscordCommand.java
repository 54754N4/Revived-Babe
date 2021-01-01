package commands.hierarchy;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.Gson;

import backup.MusicState;
import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import lib.HTTP.Method;
import lib.HTTP.MultipartRequestBuilder;
import lib.HTTP.RequestBuilder;
import lib.HTTP.ResponseHandler;
import lib.PrintBooster;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public abstract class DiscordCommand extends PrintCommand {
	protected static final Gson gson = new Gson();
	protected static final Random rand = new Random();
	private static final Set<Long> guildsVisited = new HashSet<>();
	private boolean keepAlive;
	
	public static enum Global {
		DELETE_USER_MESSAGE("-d", "--delete"), 
		DISPLAY_HELP_MESSAGE("--help", "-h"),
		PRIVATE_MESSAGE_REPLY("-rep", "--reply"),
		SHOW_EXECUTION_TIME("--timed"),
		HIDE_ALL_OUTPUT("-s", "--silent"),
		DELAYED("--after"),
		SCHEDULED("--every");
		
		public String[] params;
		
		Global(String... args) {
			this.params = args;
		}
	}
	
	public DiscordCommand(UserBot bot, Message message, String...names) {
		super(bot, message, names);
		keepAlive = false;
		if (bot != null) {	// since commands can be instantiated using dummy data
			long id = message.getGuild().getIdLong();
			if (!guildsVisited.contains(id)) {
				guildsVisited.add(id);
				MusicState.restore(bot);
			}
		}
	}
	
	protected DiscordCommand keepAlive() {
		keepAlive = true;
		return this;
	}
	
	public boolean fromMusicBot() {
		return bot instanceof MusicBot;
	}
	
	public MusicBot getMusicBot() {
		return MusicBot.class.cast(bot);
	}
	
	protected DiscordCommand backup() {
		if (fromMusicBot())
			MusicState.backup(getMusicBot());
		return this;
	}
	
	protected DiscordCommand clearBackup() throws SQLException {
		if (fromMusicBot())
			MusicState.clear(getMusicBot(), guild.getIdLong());
		return this;
	}
	
	/* Convenience methods */
	
	public boolean isOwner() {
		return message.getAuthor().getIdLong() == 188033164864782336l;
	}
	
	protected List<Role> getCallerRoles() {
		return message.getMember() == null ? new ArrayList<>() : message.getMember().getRoles();
	}
	
	protected boolean callerHasRole(Role role) {
		for (Role r : getCallerRoles()) 
			if (r.equals(role)) 
				return true;
		return false;
	}
	
	protected void addRoles(Member member, Collection<Role> roles) {
		getGuild().modifyMemberRoles(member, roles, new ArrayList<>()).queue();
	}
	
	protected void removeRoles(Member member, Collection<Role> roles) {
		getGuild().modifyMemberRoles(member, new ArrayList<>(), roles).queue();
	}
	
	protected List<Role> getRoles(String match, boolean createIfNonExistant) {
		List<Role> roles = guild.getRolesByName(match, true);
		if (roles.size() == 0 && createIfNonExistant)
			roles.add(guild.createRole().setName(StringLib.capitalize(match)).complete());
		return roles;
	}
	
	protected void removeUserMessage() {
		message.delete().queue();
	}
	
	/* Rest + multipart/form requests convenience methods */
	
	protected String urlEncode(String input) {
		return URLEncoder.encode(input, StandardCharsets.UTF_8);
	}
	
	public static <T> T restRequest(Class<T> cls, String apiFormat, Object... args) throws IOException {
		try (ResponseHandler handler = restRequest(apiFormat, args)) {
			return gson.fromJson(handler.getResponse(), cls);
		}
	}
	
	public static <T> T formRequest(Class<T> cls, Consumer<MultipartRequestBuilder> setup, String apiFormat, Object...args) throws IOException {
		try (ResponseHandler handler = formRequest(setup, apiFormat, args)) {
			return gson.fromJson(handler.getResponse(), cls);
		}
	}
	
	public static ResponseHandler restRequest(String apiFormat, Object...args) throws IOException {
		try (RequestBuilder builder = new RequestBuilder(String.format(apiFormat, args))) {
			return new ResponseHandler(builder.build());
		}
	}
	
	public static ResponseHandler formRequest(Consumer<MultipartRequestBuilder> setup, String apiFormat, Object...args) throws IOException {
		try (MultipartRequestBuilder builder = new MultipartRequestBuilder(String.format(apiFormat, args))) {
			builder.setMethod(Method.POST);
			setup.accept(builder);
			return new ResponseHandler(builder.build());
		}
	}
	
	/* help building + argument parsing*/
	
	protected String helpBuilder(String args, String... lines) {
		StringBuilder sb = new StringBuilder();
		sb.append("#"+Arrays.toString(names)+"\n").append("Usage: <name> "+args+"\n");
		String output;
		for (String line: lines) {
			output = line;
			if (line.startsWith("[") || line.startsWith("<")) 
				output = "Usage: <name> " + output;
			else if (!line.startsWith("#") && !line.startsWith("Usage")) 
				output = "\t" + output;
			sb.append(output+"\n");
		}
		return markdown(sb.toString());
	}
	
	@Override
	public Void call() throws Exception {
		try {
			long time = 0;
			if (hasArgs(Global.DELETE_USER_MESSAGE.params)) 
				removeUserMessage();
			if (hasArgs(Global.DELAYED.params)) {
				String delay = params.named.get("--after");
				if (!delay.matches("[0-9]+"))
					println("Invalid delay %s", delay);
				else {
					long l = Long.parseLong(delay);
					try {
						Thread.sleep(l*1000); // convert to seconds
					} catch (Exception e) {
						logger.error("Could not delay for "+l+"s", e);
					}
				}
			} 
			if (hasArgs(Global.DISPLAY_HELP_MESSAGE.params)) 
				print(helpMessage());
			else {
				time = System.currentTimeMillis();		
				execute(StringLib.unQuote(input.trim()).trim());
				time = System.currentTimeMillis() - time;
			}
			if (hasArgs(Global.SHOW_EXECUTION_TIME.params)) 
				println(String.format("Execution time: %d ms", time));
		} catch (Exception e) {
			println("Error during execution, check logs: `%s`", e.getMessage());
			getLogger().error(this+" thread generated "+e+" : "+e.getMessage(), e);
		} finally {
			finalise();
		} return null;
	}
	
	private void finalise() {
		if (!keepAlive)
			finished.set(true);
		if (hasArgs(Global.HIDE_ALL_OUTPUT.params)) 
			return;
		String[] tokens = PrintBooster.splitForDiscord(stdout.toString())
				.toArray(new String[0]);
		for (String token : tokens) 
			channel.sendMessage(token)
				.queue();
	}
}
