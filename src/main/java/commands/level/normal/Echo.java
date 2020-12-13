package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Echo extends DiscordCommand {

	public Echo(UserBot bot, Message msg) {
		super(bot, msg, Command.ECHO.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<text>",
			"-n or --no\tMakes me print not inside a codeblock",
			"Makes me repeat after you, Happy ?");
	}
	
	@Override
	public void execute(String command) {
		String output = hasArgs("-n", "--no") ? command : codeBlock(command);
		if (mentioned.users.size() == 0)
			print(output);
		else for (User user : mentioned.users)
			user.openPrivateChannel()
				.queue(channel -> channel.sendMessage(output).queue());
	}
	
}
