package commands.level.normal;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class Seek extends DiscordCommand {

	public Seek(UserBot bot, Message message) {
		super(bot, message, Command.SEEK.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<time>",
			"Seeks to the position given (e.g. 3600 or 2:23 or 1:2:1)");
	}
	
	@Override
	public void execute(String command) {
		MusicBot bot = getMusicBot();
		Guild guild = getGuild();
		AudioTrack current = bot.getCurrentTrack(guild);
		if (command.equals("")) {
			println("Give me time to seek to.");
			return;
		} else if (current == null) {
			println("You need to be playing a song..");
			return;
		}
		String time = command;
		long duration = current.getDuration(), position;
		if (StringLib.isInteger(time))
			position = Integer.parseInt(time)*1000;
		else 
			position = StringLib.timeToSeconds(time)*1000;
		if (position < duration) {
			bot.seekTo(guild, position);
			print("Seeked to position %s/%s (%s/%s)",
					StringLib.millisToTime(position),
					StringLib.millisToTime(duration),
					position,
					duration);
		} else 
			println("Seeked too far.. Max for current song is %s", StringLib.millisToTime(duration));
	}

}
