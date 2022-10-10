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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

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
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!shouldHandleEvent(event))
			return;
		setMessage(event.getMessage());	// overrides every new reply
		state = state.check(event);
		if (state == end) 
			onEnd(event);
	}
	
	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs(Global.DIAGRAM.params)) {
			printDiagram();
			return;
		}
		// FSM entry-point
		setKeepAlive();		// to keep sending typing action 
		attachListener();	// listens for new replies
		// Restrict handling
		if (getMentions().getUsers().size() != 0)
			getMentions().getUsers()
				.stream()
				.map(ISnowflake::getIdLong)
				.forEach(issuers::add);
		issuers.add(getMessage().getAuthor().getIdLong());
		setup();
	}
	
	// FSM don't need execute so replace with setup
	protected abstract void setup(); 
	
	protected void onEnd(MessageReceivedEvent event) {
		removeListener();
		setFinished();
	}
	
	protected boolean shouldHandleEvent(MessageReceivedEvent event) {
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

	protected static boolean matches(MessageReceivedEvent event, String match) {
		return StringLib.matchSimplified(event.getMessage().getContentDisplay(), match);
	}
	
	// For testing and printing FSMs
	private void printDiagram() throws NumberFormatException, IOException {
		String scaling = DEFAULT_SCALING,
			param = Global.DIAGRAM.params[0];
		if (hasArgs(Global.DIAGRAM.params) && !getParams().getNamed().get(param).equals(""))
			scaling = getParams().getNamed().get(param); 
		if (!StringLib.isInteger(scaling)) {
			println("Invalid integer scaling given =v.");
			return;
		}
		File diagram = FSMVisualizer.visualise(this, Integer.parseInt(scaling));
		getChannel().sendMessage("This command's FSM :")
			.addFiles(FileUpload.fromData(diagram))
			.queue();
		diagram.delete();
	}
}