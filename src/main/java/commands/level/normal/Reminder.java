package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Reminder extends DiscordCommand {

	public Reminder(UserBot bot, Message message) {
		super(bot, message, Command.REMINDER.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"");
	}

	@Override
	protected void execute(String input) throws Exception {
		// TODO Auto-generated method stub

	}

}
