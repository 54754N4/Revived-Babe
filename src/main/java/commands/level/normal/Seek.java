package commands.level.normal;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Seek extends DiscordCommand {

	public Seek(UserBot bot, Message message) {
		super(bot, message, Command.SEEK.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<time>",
			"Seeks to the position given (e.g. 02:23 or 1:2:1)");
	}
	
	@Override
	public void execute(String command) {
		MusicBot bot = getMusicBot();
		if (!command.equals("")) {
			AudioTrack current = bot.getCurrentTrack(guild); 
			if (current != null) {			
				String time = command;
				long position = StringLib.timeToSeconds(time)*1000, 
					duration = current.getDuration();
				if (position < duration) {
					bot.seekTo(guild, position);
					print("Seeked to position "+StringLib.padTime(time)+"/"+StringLib.millisToTime(duration)+" (e.g. "+position+"/"+duration+")");
				} else print("Seeked too far.. Max for current song is "+StringLib.millisToTime(duration));
			} else print("You need to be playing a song..");
		} else print("You fucked up =v, here : "+helpMessage());
	}

}
