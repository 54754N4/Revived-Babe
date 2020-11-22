package commands.level.normal;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.TrackScheduler;
import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import lib.messages.PagedTracksHandler;
import net.dv8tion.jda.api.entities.Message;

public class ListTracks extends DiscordCommand {

	public ListTracks(UserBot bot, Message message) {
		super(bot, message, Command.LS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
				"# Args",
				"-p or --paged\tmakes me list tracks as paged results",
				"List tracks in current queue.");
	}

	@Override
	protected void execute(String input) throws Exception {
		int pos = 0;
		CircularDeque queue = getMusicBot().getPlaylist(guild);
		if (queue.size() == 0) 
			println("No songs queued.");
		else if (hasArgs("-p", "--paged")) {
			final TrackScheduler scheduler = getMusicBot().getScheduler(guild);
			channel.sendMessage("Loading..")
				.queue(new PagedTracksHandler(queue)
					.handle(0x23EF, reaction -> scheduler.togglePause())
					.handle(0x23EE, reaction -> scheduler.previousTrack())
					.handle(0x23ED, reaction -> scheduler.nextTrack())
					.handle(0x1F502, reaction -> scheduler.toggleRepeating())
					.handle(0x1F501, reaction -> scheduler.toggleLooping())
					.handle(0x1F500, reaction -> scheduler.shuffle())
					.handle(0x1F932, reaction -> printlnIndependently(queue.get(queue.getCurrent()).getInfo().uri))
			);
		} else 
			for (AudioTrack track : queue)
				println(String.format(
					(pos == queue.getCurrent()) ? "%d. `%s` (%s)" : "%d. %s (%s)",
					pos++, 
					track.getInfo().title, 
					StringLib.millisToTime(track.getInfo().length)));
	}

}
