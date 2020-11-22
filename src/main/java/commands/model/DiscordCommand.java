package commands.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

import bot.model.MusicBot;
import bot.model.UserBot;
import lib.HTTP;
import lib.PrintBooster;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public abstract class DiscordCommand extends PrintCommand {
	protected static final Gson gson = new Gson();
	protected static Random rand = new Random();
	
	public static enum Global {
		DELETE_USER_MESSAGE("-d", "--delete"), 
		DISPLAY_HELP_MESSAGE("--help", "-h"),
		PRIVATE_MESSAGE_REPLY("-rep", "--reply"),
		SHOW_EXECUTION_TIME("--timed"),
		HIDE_ALL_OUTPUT("-s", "--silent");
		
		public String[] params;
		
		Global(String... args) {
			this.params = args;
		}
	}
	
	public DiscordCommand(UserBot bot, Message message, String...names) {
		super(bot, message, names);
	}
	
	public boolean fromMusicBot() {
		return bot instanceof MusicBot;
	}
	
	public MusicBot getMusicBot() {
		return MusicBot.class.cast(bot);
	}
	
	/* Convenience methods */
	
	public boolean isOwner() {
		return message.getAuthor().getIdLong() == 188033164864782336l;
	}
	
	public void actTyping() {
		channel.sendTyping().queue();
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
	
	public static <T> T restRequest(String api, Class<T> cls, Object... args) throws MalformedURLException, IOException {
		try (	// try-with-resources to auto-close close-able resources
			HTTP.RequestBuilder builder = new HTTP.RequestBuilder(String.format(api, args));
			HTTP.ResponseHandler handler = new HTTP.ResponseHandler(builder.build());
		) {
			return gson.fromJson(handler.getResponse(), cls);
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
		finished.set(true);
		if (hasArgs(Global.HIDE_ALL_OUTPUT.params)) 
			return;
		String[] tokens = PrintBooster.splitForDiscord(stdout.toString())
				.toArray(new String[0]);
		for (String token : tokens) 
			channel.sendMessage(token).queue();
	}
}
