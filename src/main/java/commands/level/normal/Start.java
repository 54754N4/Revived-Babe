package commands.level.normal;

import bot.model.Bot;
import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Start extends DiscordCommand {

	public Start(UserBot bot, Message message) {
		super(bot, message, Command.START.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<name>", 
			"Say slave if you want a new slave");
//			+ "otherwise `echo` or `mirror`.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (input.startsWith("slave"))
			Bot.Slaves.newSlave();
		else println("Cannot find bot with that name.");
	}

}
