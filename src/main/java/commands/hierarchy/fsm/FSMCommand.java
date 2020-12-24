package commands.hierarchy.fsm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import lib.FSMVisualizer;
import lib.StringLib;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class FSMCommand extends DiscordCommand {
	private static final String DEFAULT_SCALING = "2";
	protected Transition.Builder EXIT_TRANSITION;
	protected final State start, end;
	private List<Long> issuers;
	private State state;	// tracks current state
	
	public FSMCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
		state = start = new State.Builder("START").build();
		end = new State.Builder("END").build();
		EXIT_TRANSITION = new Transition.Builder()
			.setCondition(Conditions.EXIT)
			.setNextState(end);
		// DEFAULT_EXIT priority and action are to be set by child classes
		issuers = new ArrayList<>();
	}
	
	public State getStart() {
		return start;
	}
	
	public State getEnd() {
		return end;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (!shouldHandleEvent(event))
			return;
		message = event.getMessage();	// overrides every new reply
		state = state.check(event);
		if (state == end) 
			onEnd(event);
	}
	
	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("--test")) {
			printDiagram();
			return;
		}
		// FSM entry-point
		keepAlive();		// to keep sending typing action 
		attachListener();	// listens for new replies
		// Restrict handling
		if (mentioned.users.size() != 0)
			mentioned.users
				.stream()
				.map(ISnowflake::getIdLong)
				.forEach(issuers::add);
		issuers.add(message.getAuthor().getIdLong());
		setup();
	}
	
	// FSM don't need execute so replace with setup
	protected abstract void setup(); 
	
	protected void onEnd(GuildMessageReceivedEvent event) {
		removeListener();
		finished.set(true);
	}
	
	protected boolean shouldHandleEvent(GuildMessageReceivedEvent event) {
		return issuers.stream().anyMatch(id -> id == event.getAuthor().getIdLong());
	}

	/* Since thread would have died, override to print independently and
	 * not use default print-on-command-death behaviour */
	@Override
	public void println() {
		printlnIndependently();
	}
	
	@Override
	public void print(String format, Object... args) { 
		printIndependently(format, args);
	}
	
	@Override
	public void println(String format, Object... args) {
		printlnIndependently(format, args);
	}

	protected static boolean matches(GuildMessageReceivedEvent event, String match) {
		return StringLib.simpleMatch(event.getMessage().getContentDisplay(), match);
	}
	
	// For testing and printing FSMs
	private void printDiagram() throws NumberFormatException, IOException {
		String scaling = DEFAULT_SCALING; 
		// Parse 3 different ways based on user preference
		scaling = params.named.get("--test").length() != 0 ? params.named.get("--test") : scaling;
		scaling = hasArgs("--scale") ? params.named.get("--scale") : scaling;
		scaling = hasArgs("--scaling") ? params.named.get("--scaling") : scaling; 
		if (!StringLib.isInteger(scaling)) {
			println("Invalid integer scaling given =v.");
			return;
		}
		File diagram = FSMVisualizer.visualise(this, Integer.parseInt(scaling));
		channel.sendMessage("This command's FSM :").addFile(diagram).queue();
		diagram.delete();
	}
}