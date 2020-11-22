package commands.level.normal;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class SMS extends DiscordCommand {

	public SMS(UserBot bot, Message message) {
		super(bot, message, Command.SMS.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("<phone number>", "I send sms to number given.");
	}

	@Override
	protected void execute(String input) throws Exception {
		
	}

}
