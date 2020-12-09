package commands.level.admin;

import bot.hierarchy.UserBot;
import commands.hierarchy.FSMCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TestFSM extends FSMCommand {
	private int count;
	
	public TestFSM(UserBot bot, Message message) {
		super(bot, message, Command.TEST_FSM.names);
		count = 0;
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Just helps testing FSM commands");
	}
	
	@Override
	public void setup() {
		start.addTransition(new Transition.Builder()
				.setCondition(TestFSM::isNotExit)
				.setAction(event -> println("Counted %d messages", count++))
				.setNextState(start)
				.build())
			.addTransition(new Transition.Builder()
				.setCondition(TestFSM::isExit)
				.setNextState(end)
				.build());
		println("I'll start counting your replies, just say exit to stop.");
	}
	
	private static boolean isNotExit(GuildMessageReceivedEvent event) {
		return !isExit(event);
	}
	
	private static boolean isExit(GuildMessageReceivedEvent event) {
		return event.getMessage()
				.getContentDisplay()
				.toLowerCase()
				.startsWith("exit");
	}
}
