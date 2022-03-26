package commands.level.normal;

import java.util.ArrayList;
import java.util.List;

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
		else {
			List<User> users = new ArrayList<>(mentioned.users);
			for (User user : mentioned.users)
				if (user.isBot())
					users.remove(user);
			if (users.size() == 0)
				println("No non-bot user mentioned.");
			else 
				for (User user : users)
					user.openPrivateChannel()
						.queue(channel -> channel.sendMessage(output).queue());
		}
	}
	
}
