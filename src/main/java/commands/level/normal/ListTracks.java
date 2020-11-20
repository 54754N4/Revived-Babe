package commands.level.normal;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class ListTracks extends DiscordCommand {

	public ListTracks(UserBot bot, Message message) {
		super(bot, message, Command.LS.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", "List tracks in current queue.");
	}

	@Override
	protected void execute(String input) throws Exception {
		int pos = 0;
		CircularDeque queue = getMusicBot().getPlaylist(guild);
		if (queue.size() == 0) 
			println("No songs queued.");
		else 
			for (AudioTrack track : queue)
				println(String.format(
					(pos == queue.getCurrent()) ? "%d. `%s` (%s)" : "%d. %s (%s)",
					pos++, 
					track.getInfo().title, 
					StringLib.millisToTime(track.getInfo().length)));
	}

}
