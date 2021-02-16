package commands.level.normal;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.TrackScheduler;
import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import lib.messages.PagedTracksHandler;
import lib.messages.ReactionsHandler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

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
		final MusicBot bot = getMusicBot();	// give lambdas already casted
		final TrackScheduler scheduler = bot.getScheduler(guild);
		ReactionsHandler handler = new PagedTracksHandler(bot, scheduler, true)
				.handle(0x23EF, event -> scheduler.togglePause())
				.handle(0x23EE, event -> scheduler.previousTrack())
				.handle(0x23ED, event -> scheduler.nextTrack())
				.handle(0x1F502, event -> scheduler.toggleRepeating())
				.handle(0x1F501, event -> scheduler.toggleLooping())
				.handle(0x1F500, event -> scheduler.shuffle())
				.handle(0x1F517, this::currentUrl)
				.handle(0x1F508, event -> scheduler.decreaseVolume())
				.handle(0x1F50A, event -> scheduler.increaseVolume())
				.handle(0x2B07, this::moveBottom)
				.handle(0x23FA, this::joinUser)
				.handle(0x23CF, event -> bot.disconnect(guild));
		channel.sendMessage("Loading..")
			.queue(handler);
	}
	
	private void currentUrl(MessageReactionAddEvent event) {
		CircularDeque queue = getMusicBot().getPlaylist(guild);
		printlnIndependently(queue.get(queue.getCurrent()).getInfo().uri);
	}
	
	private void moveBottom(MessageReactionAddEvent event) {
		event.getChannel()
			.deleteMessageById(event.getMessageId())
			.queue(message -> createPlayer());
	}
	
	private void joinUser(MessageReactionAddEvent reaction) {
		Member member = reaction.getMember();
		if (member == null) {
			println("Could not retrieve member from reaction event.");
			return;
		}
		GuildVoiceState state = member.getVoiceState();
		if (!state.inVoiceChannel()) 
			println("%s You should be in a voice channel before you ask me to join =v.");
		else
			getMusicBot().connect(state.getChannel());
	}
	
	private String printCurrent() {
		CircularDeque queue = getMusicBot().getPlaylist(guild);
		AudioTrack track = queue.get(queue.getCurrent());
		return String.format("%d. %s (`%s`/%s) | Total Songs = `%s`", 
				queue.getCurrent(),
				track.getInfo().title, 
				StringLib.millisToTime(track.getPosition()), 
				StringLib.millisToTime(track.getDuration()),
				queue.size());
	}
	
	private void createCurrent() {
		channel.sendMessage(printCurrent())
				.queue(message -> 
					new ReactionsHandler(bot)
							.handle(0x2747, reaction -> message.editMessage(printCurrent()).queue())
							.accept(message));
	}
}
