package commands.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand.Global;
import commands.model.Mentions;
import commands.model.Params;
import commands.model.ThreadsManager;
import commands.model.TypingWatchdog;
import lib.Consumers;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter implements Callable<Void> {
	public static final int MESSAGE_MAX = Message.MAX_CONTENT_LENGTH;
	public final String[] names;
	private final Logger logger;
	private AtomicBoolean keepAlive, finished;
	private UserBot bot;					// bot responder
	private Guild guild;
	private Message message;				// message trigger
	private MessageChannel channel;
	private StringBuilder stdout;
	// The following attributes are set when start() is called
	private String input;
	private Params params;				// named + unnamed parameters
	private Mentions mentioned;			// mentioned users/channels
	private Future<?> thread;			// future of current thread
	
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
		finished = new AtomicBoolean();
		stdout = new StringBuilder();
		logger = LoggerFactory.getLogger(getClass());
	}
	
	/* Accessors and Setters */
	
	public boolean isFinished() {
		return finished.get();
	}
	
	public boolean keepAlive() {
		return keepAlive.get();
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
	
	public Message getMessage() {
		return message;
	}
	
	protected Command setMessage(Message message) {
		this.message = message;
		return this;
	}
	
	public MessageChannel getChannel() {
		return channel;
	}
	
	public StringBuilder getStdout() {
		return stdout;
	}
	
	public String getInput() {
		return input;
	}
	
	public Params getParams() {
		return params;
	}
	
	public Mentions getMentions() {
		return mentioned;
	}
	
	public Future<?> getThread() {
		return thread;
	}
	
	/* Convenience methods */
	
	protected Command kill() {
		keepAlive.set(false);
		finished.set(true);
		return this;
	}
	
	protected Command setKeepAlive() {
		keepAlive.set(true);
		return this;
	}
	
	protected Command setFinished() {
		finished.set(true);
		return this;
	}
	
	public void actTyping() {
		channel.sendTyping()
			.queue(Consumers::ignore, Consumers::ignore);
	}
	
	protected void clearStdout() {
		stdout.delete(0, stdout.length());
	}
	
	/* Args/input handling */
	
	protected boolean hasArgs(String... args) {
		for (String param : params.getAll()) 
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
		mentioned = new Mentions(bot, message, command);			// filter mentions from input first
		input = treatInput(mentioned.getFilteredMessage());	// then cleanup input
		channel = hasArgs(Global.PRIVATE_MESSAGE_REPLY.params) ? 
				message.getAuthor().openPrivateChannel().complete()
				: message.getChannel();
		TypingWatchdog.handle(this);
		thread = ThreadsManager.POOL.submit(this);
		logger.info("Started command {} thread", getClass());
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
