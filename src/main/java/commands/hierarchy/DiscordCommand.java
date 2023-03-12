package commands.hierarchy;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import backup.MusicState;
import backup.Reminders;
import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.model.Invoker;
import commands.model.Invoker.Reflector;
import commands.model.ThreadSleep;
import lib.Consumers;
import lib.PrintBooster;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public abstract class DiscordCommand extends Command {
	private static final Map<UserBot, Set<Long>> GUILDS_VISITED = new ConcurrentHashMap<>();	// keep track per bot
	private static final String[] SCHEDULING_STOP_VERBS = { "abort", "stop", "kill", "shutdown" };
	protected static final Random rand = new Random();
	
	private long executionTime;
	
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
	
	/* Convenience methods */
	
	public boolean fromGuild() {
		Message message = getMessage();
		return message == null ? false : message.isFromGuild();
	}
	
	public boolean fromMusicBot() {
		return MusicBot.class.isInstance(getBot());
	}
	
	public MusicBot getMusicBot() {
		return MusicBot.class.cast(getBot());
	}
	
	protected DiscordCommand clearBackup() throws SQLException {
		if (fromMusicBot())
			MusicState.clear(getMusicBot(), getGuild().getIdLong());
		return this;
	}
	
	public boolean isOwner() {
		return getMessage().getAuthor().getIdLong() == 188033164864782336l;
	}

	protected void removeUserMessage() {
		getMessage().delete().queue(Consumers::ignore, Consumers::ignore);
	}
	
	public void eval(String input) {
		Reflector.Type type = isOwner() ? Reflector.Type.ALL : Reflector.Type.NORMAL;
		Invoker.invoke(getBot(), getMessage(), input, type);
	}
	
	protected boolean hasAttachment() {
		return getMessage().getAttachments().size() != 0;
	}
	
	/* Scheduling */
	
	private DiscordCommand addKillHandler() {
		addReplyHandler(new ReplyHandler.Builder()
			.ignoreBots()
			.authorOnly()
			.ifTrue((text, cmd) ->
				StringLib.matchesSimplified(text, SCHEDULING_STOP_VERBS) &&
				StringLib.matchesSimplified(text, names)
			)
			.then(cmd -> cmd.kill())
			.println("Aborting command %s", getClass().getName())
			.build());
		return this;
	}
	
	private void schedule() throws Exception {
		addKillHandler()
			.attachListener()	// make command listen for replies
			.setKeepAlive();		// prevent killing on finalisation
		// Parse period input
		String param = getParams().getNamed().get("--every");
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
				clearStdout();	// reset stdout
			}
		};
		// Do scheduling logic
		Callable<Void> sleeper = ThreadSleep.nonBlocking(period, this);
		if (instant)
			iteration.accept(getInput());
		while (true) {
			sleeper.call();
			if (isFinished())
				break;
			iteration.accept(getInput());
		}
		removeListener();	// stop listening to incoming messages
	}
	
	/* Thread execution methods */
	
	@Override
	public Command start(String command) {
		if (getBot() != null && getMessage().getChannelType().isGuild()) {	// since commands can be instantiated using dummy data
			getMessage().getChannel()
				.sendTyping()
				.queue(Consumers::ignore, Consumers::ignore);
			long id = getGuild().getIdLong();
			GUILDS_VISITED.putIfAbsent(getBot(), new HashSet<>());
			Set<Long> visited = GUILDS_VISITED.get(getBot());
			if (!visited.contains(id)) { // only restore backups if not visited
				visited.add(id);
				Reminders.restoreAll(getBot().getJDA());
				if (fromMusicBot())
					MusicState.restore(getMusicBot());
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
			println("Only the following roles are allowed : %s", Arrays.toString(getAllowedRoles().toArray(String[]::new)));
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
			String after = getParams().getNamed().get("--after");
			if (!after.matches("[0-9]+"))
				println("Invalid delay %s", after);
			else {
				final long delay = Long.parseLong(after)*1000; // convert from seconds
				ThreadSleep.nonBlocking(delay, this).call();
				if (isFinished())	// has been aborted
					return null;	// prevent execution
			}
		}
		// Single execution
		try { execute(getInput()); } 
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
		if (!keepAlive())
			setFinished();
		if (hasArgs(Global.SHOW_EXECUTION_TIME.params))
			println(String.format("Execution time: %d ms", executionTime));
		if (hasArgs(Global.HIDE_ALL_OUTPUT.params)) 
			return;
		String[] tokens = PrintBooster.split(getStdout().toString())
				.toArray(new String[0]);
		for (String token : tokens) 
			getChannel().sendMessage(token)
				.queue();
	}
}
