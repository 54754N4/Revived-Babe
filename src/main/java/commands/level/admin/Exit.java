package commands.level.admin;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Exit extends DiscordCommand {
	public Exit(UserBot bot, Message message) {
		super(bot, message, Command.EXIT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Makes bot exit (if sent to @Babe, then will shutdown all)");
	}

	@Override
	protected void execute(String input) throws Exception {
		printlnIndependently("Farewell!");
		Thread.sleep(300);
		System.exit(UserBot.EXIT_SUCCESS);
	}
}
