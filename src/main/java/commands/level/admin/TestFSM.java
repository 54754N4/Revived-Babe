package commands.level.admin;

import bot.model.UserBot;
import commands.hierarchy.FSMCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class TestFSM extends FSMCommand {
	private State start, end;
	private int count;
	
	public TestFSM(UserBot bot, Message message) {
		super(bot, message, Command.TEST_FSM.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Just helps testing FSM commands");
	}
	
	@Override
	protected State startState() {
		return start;
	}

	@Override
	protected State exitState() {
		return end;
	}
	
	@Override
	public void onStart() {
		count = 0;
		start = new State.Builder()
				.addTransition(new Transition.Builder()
						.setPassthrough()
						.setAction(event -> println("Counted %d messages", count++))
						.setNextState(start)
						.build())
				.addTransition(new Transition.Builder()
						.setCondition(event -> event.getMessage()
								.getContentDisplay()
								.toLowerCase()
								.startsWith("exit"))
						.setNextState(end)
						.build())
				.build();
		end = new State.Builder().build(); 
	}
}
