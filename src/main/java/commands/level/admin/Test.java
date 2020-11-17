package commands.level.admin;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Test extends DiscordCommand {
	public Test(UserBot bot, Message message) {
		super(bot, message, Command.TEST.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", "Just used for testing");
	}

	@Override
	protected void execute(String input) throws Exception {
		println("Bullshit");
	}
}
