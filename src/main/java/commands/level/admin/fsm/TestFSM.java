package commands.level.admin.fsm;

import bot.hierarchy.UserBot;
import commands.hierarchy.fsm.Conditions;
import commands.hierarchy.fsm.FSMCommand;
import commands.hierarchy.fsm.Transition;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

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
				.setCondition(Conditions.NOT_EXIT)
				.setAction(event -> println("Counted %d messages", ++count))
				.setNextState(start)
				.build())
			.addTransition(EXIT_TRANSITION.build());
		println("I'll start counting your replies, just say exit to stop.");
	}
}
