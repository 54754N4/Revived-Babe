package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Previous extends DiscordCommand {
	public Previous(UserBot bot, Message message) {
		super(bot, message, Command.PREVIOUS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Plays previous track (if there is one).");
	}

	@Override
	protected void execute(String input) throws Exception {
		getMusicBot().previous(guild);
		println("Playing previous track");
	}
}
