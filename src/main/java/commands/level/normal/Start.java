package commands.level.normal;

import bot.hierarchy.Bot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Start extends DiscordCommand {

	public Start(UserBot bot, Message message) {
		super(bot, message, Command.START.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<name>", 
			"Say 'slave' if you want a new slave or 'echo' for voice test bot.");
//			+ "otherwise `echo` or `mirror`.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (input.startsWith("slave"))
			Bot.Slaves.newSlave();
		else if (input.startsWith("echo"))
			Bot.startEcho();
		else 
			println("Cannot find bot with that name.");
	}

}
