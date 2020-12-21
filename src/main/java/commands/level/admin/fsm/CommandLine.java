package commands.level.admin.fsm;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import bot.hierarchy.UserBot;
import commands.hierarchy.fsm.FSMCommand;
import commands.hierarchy.fsm.Transition;
import commands.name.Command;
import lib.StringLib;
import lib.ThreadOutput;
import lib.ThreadsManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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

	protected boolean isNotForBot(GuildMessageReceivedEvent event) {
		return !StringLib.startsWithPrefix(event.getMessage().getContentDisplay(), bot.getPrefixes());
	}
	
	private void onExecuteCommand(GuildMessageReceivedEvent event) {
		String[] input = event.getMessage().getContentDisplay().split(" ");
		try {
			Process process = ThreadsManager.execute(input);
			ThreadOutput out = ThreadsManager.read(process);
			println("%s%n%s%nExit: %d", 
					out.getOutput().trim(), 
					out.getError().trim(), 
					process.waitFor());
		} catch (IOException | InterruptedException | ExecutionException e) {
			println("Error: %s", e.getMessage());
		}
	}
}
