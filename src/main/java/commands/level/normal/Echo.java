package commands.level.normal;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Echo extends DiscordCommand {

	public Echo(UserBot bot, Message msg) {
		super(bot, msg, Command.ECHO.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<text>",
			"Makes me repeat after you, Happy ?");
	}
	
	@Override
	public void execute(String command) {
		print(codeBlock(command));
	}
	
}
