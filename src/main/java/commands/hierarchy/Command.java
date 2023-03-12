package commands.hierarchy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter 
		implements PrintCommand, RoleCommand, ListenerCommand, RestCommand {
	
	public final String[] names;
	private final Logger logger;
	private AtomicBoolean keepAlive, finished;
	private UserBot bot;					// bot responder
	private Guild guild;
	private Message message;				// message trigger
	private MessageChannel channel;
	private StringBuilder stdout;
	private Set<String> allowedRoles;
	private List<ReplyHandler> replyHandlers;
	// The following attributes are set when Command::start is called
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
		allowedRoles = new HashSet<>();
		replyHandlers = new ArrayList<>();
	}
	
	/* Accessors and Setters */
	
	@Override
	public String[] getNames() {
		return names;
	}
	
	@Override
	public Set<String> getAllowedRoles() {
		return allowedRoles;
	}
	
	@Override
	public List<ReplyHandler> getReplyHandlers() {
		return replyHandlers;
	}
	
	@Override
	public boolean isFinished() {
		return finished.get();
	}
	
	@Override
	public boolean keepAlive() {
		return keepAlive.get();
	}
	
	@Override
	public Logger getLogger() {
		return logger;
	}
	
	@Override
	public UserBot getBot() {
		return bot;
	}
	
	@Override
	public Guild getGuild() {
		return guild;
	}
	
	@Override
	public Message getMessage() {
		return message;
	}
	
	protected Command setMessage(Message message) {
		this.message = message;
		return this;
	}
	
	@Override
	public MessageChannel getChannel() {
		return channel;
	}
	
	@Override
	public StringBuilder getStdout() {
		return stdout;
	}
	
	@Override
	public String getInput() {
		return input;
	}
	
	@Override
	public Params getParams() {
		return params;
	}
	
	@Override
	public Mentions getMentions() {
		return mentioned;
	}
	
	@Override
	public Future<?> getThread() {
		return thread;
	}
	
	/* Convenience methods */
	
	@Override
	public Command kill() {
		keepAlive.set(false);
		finished.set(true);
		return this;
	}
	
	@Override
	public Command setKeepAlive() {
		keepAlive.set(true);
		return this;
	}
	
	@Override
	public Command setFinished() {
		finished.set(true);
		return this;
	}
	
	@Override
	public void actTyping() {
		channel.sendTyping()
			.queue(Consumers::ignore, Consumers::ignore);
	}
	
	@Override
	public void clearStdout() {
		stdout.delete(0, stdout.length());
	}
	
	/* ListenerCommand method implementation for dispatching */
	
	@Override
	public void dispatch(ReplyHandler handler, boolean isBot, boolean isAuthor, String text) {
		if (handler.isHandleBots() != isBot	||			// bot dispatching (handleBots ^ isBot = XOR)
			(handler.isAuthorOnly() && !isAuthor) ||	// don't dispatch on state 10 in truth table	
			!handler.getPredicate().test(text, this))
			return;
		handler.getActions().forEach(action -> action.accept(this));
	}
	
	/* Args/input handling */
	
	@Override
	public boolean hasArgs(String... args) {
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
	
	@Override
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
}
