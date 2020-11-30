package commands.level.admin;

import bot.model.UserBot;
import commands.model.Invoker;
import commands.model.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Update extends DiscordCommand {
	public Update(UserBot bot, Message message) {
		super(bot, message, Command.UPDATE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"-cmds or --commands\t Makes me re-discover newly created commands (without restart)",
			"Used for updating specific things. No shit lol.");
	}

	@Override
	protected void execute(String input) throws Exception {
		String message = "Please give me a parameter.";
		if (hasArgs("-cmds", "--commands")) {
			Invoker.Reflector.update();
			message = "Successfully updated commands list.";
		}
		println(message);
	}
}
