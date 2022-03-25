package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Poll extends DiscordCommand {

	public Poll(UserBot bot, Message message) {
		super(bot, message, Command.POLL.names);
	}

	@Override
	public String helpMessage() {
		return null;
	}

	@Override
	protected void execute(String input) throws Exception {
		
	}

}
