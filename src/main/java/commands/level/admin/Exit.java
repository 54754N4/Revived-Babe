package commands.level.admin;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Exit extends DiscordCommand {
	public Exit(UserBot bot, Message message) {
		super(bot, message, Command.EXIT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
			"-n or --now\tmakes the bot shutdown faster",
			"Makes bot exit (if sent to @Babe, then will shutdown all)");
	}

	@Override
	public void execute(String input) throws Exception {
		printlnIndependently("Exiting");
		Thread.sleep(2000);
		getBot().kill(hasArgs("-n", "--now"));
	}
}
