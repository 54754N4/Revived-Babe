package commands.level.normal;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class Clear extends DiscordCommand {
	public Clear(UserBot bot, Message message) {
		super(bot, message, Command.CLEAR.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
				"-b or --backup\t\tClears backed up queue",
				"-f or --force\t\tForces the clear of even the currently playing song",
				"Discards all tracks from the queue except for the currently playing song, otherwise clears everything.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-b", "--backup")) {
			clearBackup();
			println("Cleared backed up queue");
			return;
		}
		MusicBot bot = getMusicBot();
		Guild guild = getGuild();
		String output = "Cleared all tracks from queue";
		if (hasArgs("-f", "--force")) {
			output += " forcibly";
			bot.stop(guild);
		}
		bot.clear(guild);
		println(output);
	}
}
