package commands.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.UserBot;
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
	public AtomicBoolean finished;			// stores cmd execution state
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
	protected Future<Void> thread;			// future of current thread
	
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
		finished = new AtomicBoolean();
		stdout = new StringBuilder();
		logger = LoggerFactory.getLogger(getClass());
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
	
	public Future<Void> getThread() {
		return thread;
	}
	
	public boolean isFinished() {
		return finished.get();
	}
	
	public void actTyping() {
		channel.sendTyping().queue();
	}
	
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
			if (arg.startsWith("--")) target = named;
			else if (arg.startsWith("-") && !arg.equals("-")) target = unnamed;
			else target = nonArgs;
			target.add(arg);
		}
		params = new Params(named, unnamed);
		return nonArgs.toArray(new String[nonArgs.size()]);
	}
	
	public Command start(String command) { 
		mentioned = new Mentions(message, command);				// filter mentions from input first
		input = StringLib.consumeName(StringLib.join(setParams(mentioned.filteredMessage)), names); 	// then args + name
		channel = hasArgs(Global.PRIVATE_MESSAGE_REPLY.params) ? 
				message.getAuthor().openPrivateChannel().complete()
				: message.getChannel();
		thread = ThreadsManager.POOL.submit(this);
		TypingWatchdog.handle(this);
		getLogger().info("Started command "+getClass()+" thread");
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
