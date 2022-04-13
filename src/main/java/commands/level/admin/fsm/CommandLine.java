package commands.level.admin.fsm;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import bot.hierarchy.UserBot;
import commands.hierarchy.fsm.FSMCommand;
import commands.hierarchy.fsm.Transition;
import commands.model.ThreadOutput;
import commands.model.ThreadsManager;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLine extends FSMCommand {

	public CommandLine(UserBot bot, Message message) {
		super(bot, message, Command.COMMAND_LINE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
			"Only for my owner.");
	}
	
	@Override
	protected void setup() {
		start.addTransition(new Transition.Builder()
				.setCondition(this::isNotForBot)
				.setAction(this::onExecuteCommand)
				.setNextState(start)
				.build())
			.addTransition(EXIT_TRANSITION
				.setPriority(1)
				.setAction(e -> println("Exited terminal."))
				.build());
	}

	protected boolean isNotForBot(MessageReceivedEvent event) {
		return !StringLib.startsWith(event.getMessage().getContentDisplay(), getBot().getPrefixes());
	}
	
	private void onExecuteCommand(MessageReceivedEvent event) {
		String[] input = event.getMessage().getContentDisplay().split(" ");
		try {
			Process process = ThreadsManager.execute(input);
			ThreadOutput out = ThreadsManager.read(process);
			println("%s%n%s%nExit: %d", 
					out.getOutput().trim(), 
					out.getError().trim(), 
					process.waitFor());
			process.destroy();
		} catch (IOException | InterruptedException | ExecutionException e) {
			println("Error: %s", e.getMessage());
		}
	}
}
