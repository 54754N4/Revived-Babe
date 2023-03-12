package commands.level.normal;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class Stop extends DiscordCommand {

	public Stop(UserBot bot, Message message) {
		super(bot, message, Command.STOP.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Stops currently playing track.");
	}

	@Override
	public void execute(String input) throws Exception {
		MusicBot musicBot = getMusicBot();
		Guild guild = getGuild();
		boolean wasPaused = musicBot.isPaused(guild);
		musicBot.stop(guild);
		println(wasPaused ? "I am paused anyways.." : "Stopped playing tracks");
	}

}
