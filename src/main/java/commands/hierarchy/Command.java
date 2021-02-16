package commands.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand.Executable;
import commands.hierarchy.DiscordCommand.Global;
import commands.model.Mentions;
import commands.model.Params;
import commands.model.ThreadsManager;
import commands.model.TypingWatchdog;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter implements Callable<Void> {
	public static final int MESSAGE_MAX = Message.MAX_CONTENT_LENGTH;
	public final String[] names;
	protected AtomicBoolean keepAlive, 
		scheduled, 
		finished;			// stores cmd execution state
	protected final Logger logger;
	protected UserBot bot;					// bot responder
	protected Guild guild;
	protected Message message;				// message trigger
	protected MessageChannel channel;
	protected StringBuilder stdout;
	// The following attributes are set when start() is called
	protected String input;
	protected Params params;				// named + unnamed parameters
	protected Mentions mentioned;			// mentioned users/channels
	protected Future<?> thread;			// future of current thread
	protected long executionTime;
	
	public Command(UserBot bot, Message message, String...names) {
		this.bot = bot;
		this.message = message;
		this.names = names;
		try {
			if (message != null && message.getGuild() != null)
				guild = message.getGuild();
		} catch (Exception e) {
			// because getGuild() throws if sent in private message chat
		}
		keepAlive = new AtomicBoolean();
		scheduled = new AtomicBoolean();
		finished = new AtomicBoolean();
		stdout = new StringBuilder();
		logger = LoggerFactory.getLogger(getClass());
	}
	
	protected Command keepAlive() {
		keepAlive.set(true);
		return this;
	}
	
	protected Command kill() {
		keepAlive.set(false);
		finished.set(true);
		return this;
	}
	
	protected Logger getLogger() {
		return logger;
	}
	
	public UserBot getBot() {
		return bot;
	}
	
	public Guild getGuild() {
		return guild;
	}
	
	public Future<?> getThread() {
		return thread;
	}
	
	public boolean isFinished() {
		return finished.get();
	}
	
	public void actTyping() {
		channel.sendTyping().queue();
	}
	
	protected void clear() {
		stdout = new StringBuilder();
	}
	
	/* Exception handling convenience methods */
	
	public Command tryExecuting(Executable consumer) {
		return tryExecuting(consumer, null);
	}
	
	public Command tryExecuting(Executable consumer, String errorMessage) {
		return tryExecuting(consumer, null, errorMessage);
	}
	
	public Command tryExecuting(Executable consumer, Consumer<Throwable> errorConsumer, String errorMessage) {
		try { consumer.invoke(); }
		catch (Exception e) {
			if (errorConsumer != null)
				errorConsumer.accept(e);
			String message = errorMessage == null ? e.getMessage() : errorMessage;
			logger.error(message, e);
		}
		return this;
	}
	
	/* Args/input handling */
	
	protected boolean hasArgs(String... args) {
		for (String param : params.all) 
			if (StringLib.matches(param, args)) 
				return true;
		return false;
	}
	
	private String[] setParams(String[] args) {		// takes params only and returns filtered input
		final List<String> nonArgs = new ArrayList<>(),
			named = new ArrayList<>(),
			unnamed = new ArrayList<>();
		for (String arg : args) {
			List<String> target;
			if (arg.startsWith("--")) 
				target = named;
			else if (arg.startsWith("-") && !arg.equals("-")) 
				target = unnamed;
			else 
				target = nonArgs;
			target.add(arg);
		}
		params = new Params(named, unnamed);
		return nonArgs.toArray(new String[nonArgs.size()]);
	}
	
	private String treatInput(String...inputs) {
		String input = StringLib.consumeName(StringLib.join(setParams(inputs)), names);	// separate out parameters
		return StringLib.unQuote(input.trim()).trim(); // remove explicit quotes and trailing spaces
	}
	
	public Command start(String command) {
		mentioned = new Mentions(message, command);		// filter mentions from input first
		input = treatInput(mentioned.filteredMessage);	// then cleanup input
		channel = hasArgs(Global.PRIVATE_MESSAGE_REPLY.params) ? 
				message.getAuthor().openPrivateChannel().complete()
				: message.getChannel();
		TypingWatchdog.handle(this);
		thread = ThreadsManager.POOL.submit(this);
		getLogger().info("Started command {} thread", getClass());
		return this;
	}
	
	public abstract String helpMessage();
	protected abstract void execute(String input) throws Exception;
	
	public static class Comparator implements java.util.Comparator<Command> {
		@Override
		public int compare(Command cmd0, Command cmd1) {
			return cmd0.names[0].compareTo(cmd1.names[0]);
		}
	}
}
