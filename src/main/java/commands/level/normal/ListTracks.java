package commands.level.normal;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.TrackScheduler;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import lib.messages.PagedTracksHandler;
import lib.messages.ReactionsHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

public class ListTracks extends DiscordCommand {
	
	public ListTracks(UserBot bot, Message message) {
		super(bot, message, Command.LS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
				"# Args",
				"-c or --current\tmakes me show currently playing song",
				"-p or --paged\tmakes me list tracks as paged results",
				"List tracks in current queue.");
	}

	@Override
	protected void execute(String input) throws Exception {
		int pos = 0;
		CircularDeque queue = getMusicBot().getPlaylist(guild);
		if (queue.size() == 0) 
			println("No songs queued.");
		else if (hasArgs("-p", "--paged")) 
			createPlayer();
		else if (hasArgs("-c", "--current"))
			createCurrent();
		else 
			for (AudioTrack track : queue)
				println(String.format(
					(pos == queue.getCurrent()) ? "%d. `%s` (%s)" : "%d. %s (%s)",
					pos++, 
					track.getInfo().title, 
					StringLib.millisToTime(track.getInfo().length)));
	}
	
	private void createPlayer() {
		final CircularDeque queue = getMusicBot().getPlaylist(guild);
		final TrackScheduler scheduler = getMusicBot().getScheduler(guild);
		ReactionsHandler handler = new PagedTracksHandler(bot, scheduler, true)
				.handle(0x23EF, reaction -> scheduler.togglePause())
				.handle(0x23EE, reaction -> scheduler.previousTrack())
				.handle(0x23ED, reaction -> scheduler.nextTrack())
				.handle(0x1F502, reaction -> scheduler.toggleRepeating())
				.handle(0x1F501, reaction -> scheduler.toggleLooping())
				.handle(0x1F500, reaction -> scheduler.shuffle())
				.handle(0x1F932, reaction -> printlnIndependently(queue.get(queue.getCurrent()).getInfo().uri))
				.handle(0x1F508, reaction -> scheduler.decreaseVolume())
				.handle(0x1F50A, reaction -> scheduler.increaseVolume())
				.handle(0x2B07, this::moveBottom);
		channel.sendMessage("Loading..")
			.queue(handler);
	}
	
	private void moveBottom(MessageReaction reaction) {
		reaction.getChannel()
			.deleteMessageById(reaction.getMessageId())
			.queue(message -> createPlayer());
	}
	
	private String printCurrent() {
		CircularDeque queue = getMusicBot().getPlaylist(guild);
		AudioTrack track = queue.get(queue.getCurrent());
		return String.format("%d. %s (`%s`/%s)", 
				queue.getCurrent(),
				track.getInfo().title, 
				StringLib.millisToTime(track.getPosition()), 
				StringLib.millisToTime(track.getDuration()));
	}
	
	private void createCurrent() {
		channel.sendMessage(printCurrent())
				.queue(message -> 
					new ReactionsHandler(bot)
							.handle(0x2747, reaction -> message.editMessage(printCurrent()).queue())
							.accept(message));
	}
}
