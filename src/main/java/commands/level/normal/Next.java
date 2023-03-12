package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Next extends DiscordCommand {
	public Next(UserBot bot, Message message) {
		super(bot, message, Command.NEXT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Skips current track and plays next one (if there is one).");
	}

	@Override
	public void execute(String input) throws Exception {
		getMusicBot().next(getGuild());
		println("Skipped to next track");
	}
}
