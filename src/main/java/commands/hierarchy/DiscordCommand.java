package commands.hierarchy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.gson.Gson;

import backup.MusicState;
import backup.Reminders;
import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.model.Invoker;
import commands.model.Invoker.Reflector;
import commands.model.ThreadSleep;
import lib.Consumers;
import lib.HTTP.Method;
import lib.HTTP.MultipartRequestBuilder;
import lib.HTTP.RequestBuilder;
import lib.HTTP.ResponseHandler;
import lib.PrintBooster;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public abstract class DiscordCommand extends ListenerCommand {
	private static final Map<UserBot, Set<Long>> GUILDS_VISITED = new ConcurrentHashMap<>();	// keep track per bot
	private static final String[] SCHEDULING_STOP_VERBS = { "abort", "stop", "kill", "shutdown" };
	protected static final Random rand = new Random();
	protected static final Gson gson = new Gson();
	
	public static enum Global {
		// Normal commands global params
		DELETE_USER_MESSAGE("-d", "--delete"), 
		DISPLAY_HELP_MESSAGE("--help", "-h"),
		PRIVATE_MESSAGE_REPLY("-rep", "--reply"),
		SHOW_EXECUTION_TIME("--timed"),
		HIDE_ALL_OUTPUT("-s", "--silent"),
		DELAYED("--after"),
		SCHEDULED("--every"),
		// FSM global params
		DIAGRAM("--diagram");
		
		public String[] params;
		
		Global(String...args) {
			this.params = args;
		}
	}
	
	public DiscordCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
	}
	
	public boolean fromGuild() {
		return message == null ? false : message.isFromGuild();
	}
	
	public boolean fromMusicBot() {
		return bot instanceof MusicBot;
	}
	
	public MusicBot getMusicBot() {
		return MusicBot.class.cast(bot);
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

	protected void removeUserMessage() {
		message.delete().queue(Consumers::ignore, Consumers::ignore);
	}
	
	public void eval(String input) {
		Reflector.Type type = isOwner() ? Reflector.Type.ALL : Reflector.Type.NORMAL;
		Invoker.invoke(bot, message, input, type);
	}
	
	/* Rest + multipart/form requests convenience methods */
	
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
	
	/* Scheduling */
	
	private DiscordCommand addKillHandler() {
		addReplyHandler(new ReplyHandler.Builder()
			.ignoreBots()
			.authorOnly()
			.ifTrue((text, cmd) -> 
				cmd.scheduled.get() && 
				StringLib.matchesSimplified(text, SCHEDULING_STOP_VERBS) &&
				StringLib.matchesSimplified(text, names)
			)
			.then(Command::kill)
			.println("Aborting command %s", getClass().getName())
			.build());
		return this;
	}
	
	private void schedule() throws Exception {
		scheduled.set(true);
		addKillHandler()
			.attachListener()	// make command listen for replies
			.keepAlive();		// prevent killing on finalisation
		// Parse period input
		String param = params.named.get("--every");
		boolean instant = param.startsWith("*");
		final long period = Long.parseLong(instant ? param.substring(1) : param) * 1000,
			initial = System.currentTimeMillis();
		// Prepare function to keep executing
		Consumer<String> iteration = (input) -> {
			try { execute(input); } 
			catch (Exception e) {
				print("Error during execution: %s%n", e.getMessage());
				kill();	// stop command execution
			} finally {
				executionTime = System.currentTimeMillis() - initial;
				finalise();	// finalise and send output
				clear();	// reset stdout
			}
		};
		// Do scheduling logic
		Callable<Void> sleeper = ThreadSleep.nonBlocking(period, this);
		if (instant) {
			iteration.accept(input);
			sleeper.call();
		}
		while (!finished.get()) {
			iteration.accept(input);
			sleeper.call();
		}
		removeListener();	// stop listening to incoming messages
	}
	
	/* Thread execution methods */
	
	@Override
	public Command start(String command) {
		if (bot != null && message.getChannelType().isGuild()) {	// since commands can be instantiated using dummy data
			message.getChannel().sendTyping().queue(Consumers::ignore, Consumers::ignore);
			long id = message.getGuild().getIdLong();
			GUILDS_VISITED.putIfAbsent(bot, new HashSet<>());
			Set<Long> visited = GUILDS_VISITED.get(bot);
			if (!visited.contains(id)) { // only restore backups if not visited
				visited.add(id);
				MusicState.restore(bot);
				Reminders.restoreAll(message.getChannel());
			}
		}
		return super.start(command);
	}
	
	@Override
	public Void call() throws Exception {
		if (hasArgs(Global.DELETE_USER_MESSAGE.params)) 
			removeUserMessage();
		executionTime = System.currentTimeMillis();	// start time
		if (!callerAllowed()) {
			println("Only the following roles are allowed : %s", Arrays.toString(getAllowedRoles()));
			finalise();
			return null;
		} else if (hasArgs(Global.DISPLAY_HELP_MESSAGE.params)) {
			print(helpMessage());
			finalise();
			return null;
		} else if (hasArgs(Global.SCHEDULED.params)) {
			schedule();
			finalise();
			return null;
		} else if (hasArgs(Global.DELAYED.params)) {
			addKillHandler().attachListener();
			String after = params.named.get("--after");
			if (!after.matches("[0-9]+"))
				println("Invalid delay %s", after);
			else {
				final long delay = Long.parseLong(after)*1000; // convert from seconds
				ThreadSleep.nonBlocking(delay, this).call();
				if (finished.get())	// has been aborted
					return null;	// prevent execution
			}
		}
		// Single execution
		try { execute(input); } 
		catch (Exception e) {
			println("Error during execution: `%s`", e.getMessage());
			getLogger().error(this+" thread generated "+e+" : "+e.getMessage(), e);
		} finally {
			executionTime = System.currentTimeMillis() - executionTime;	// end time
			finalise();
		}
		return null;
	}
	
	private void finalise() {
		if (!keepAlive.get())
			finished.set(true);
		if (hasArgs(Global.SHOW_EXECUTION_TIME.params))
			println(String.format("Execution time: %d ms", executionTime));
		if (hasArgs(Global.HIDE_ALL_OUTPUT.params)) 
			return;
		String[] tokens = PrintBooster.splitForDiscord(stdout.toString())
				.toArray(new String[0]);
		for (String token : tokens) 
			channel.sendMessage(token)
				.queue();
	}
}
