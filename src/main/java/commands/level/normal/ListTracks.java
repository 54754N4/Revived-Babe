package commands.level.normal;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
import audio.TrackScheduler;
import audio.track.handlers.PagedTracksHandler;
import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import lib.messages.PagedHandler;
import lib.messages.ReactionsHandler;
import net.dv8tion.jda.api.entities.Guild;
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
				"-p or --player\tmakes me create a player with buttons",
				"-l or --local\tmakes me list tracks from local directory",
				"--playlist=P\twhere P is the playlist to list",
				"List tracks in current queue.");
	}

	@Override
	public void execute(String input) throws Exception {
		Guild guild = getGuild();
		MusicBot bot = getMusicBot();
		String playlist = bot.getCurrentPlaylist(guild);
		if (hasArgs("--playlist"))
			playlist = getParams().getNamed().get("--playlist");
		int pos = 0;
		Map<String, CircularDeque> queues = getMusicBot().getPlaylists(guild);
		CircularDeque queue = queues.get(playlist);
		if (queue == null)
			println("Playlist does not exist `%s`", playlist);
		else if (hasArgs("-l", "--local"))
			listLocal(input);
		else if (queue.size() == 0)
			println("No songs queued.");
		else if (hasArgs("-p", "--player"))
			createPlayer();
		else if (hasArgs("-c", "--current"))
			createCurrent();
		else {
			println("Listing playlist: `%s`", playlist);
			for (AudioTrack track : queue)
				println(String.format(
					(pos == queue.getCurrent()) ? "%d. `%s` (%s)" : "%d. %s (%s)",
					pos++,
					track.getInfo().title,
					StringLib.millisToTime(track.getInfo().length)));
		}
	}
	
	private void createPlayer() {
		final MusicBot bot = getMusicBot();	// give lambdas already casted
		final Guild guild = getGuild();
		final TrackScheduler scheduler = bot.getScheduler(guild);
		ReactionsHandler handler = new PagedTracksHandler(bot, scheduler, null)
				.isPlayer()
				.setTitleSuffix(this::getCurrentStats)
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
		getChannel().sendMessage("Loading..")
			.queue(handler);
	}
	
	private String getCurrentStats() {
		final MusicBot bot = getMusicBot();
		final Guild guild = getGuild();
		final AudioPlayer player = bot.getPlayer(guild);
		final TrackScheduler scheduler = bot.getScheduler(guild);
		final CircularDeque queue = bot.getPlaylist(guild);
		StringBuilder sb = new StringBuilder("| ");
		sb.append("Playlist: " + scheduler.getCurrentPlaylist() + " | ");
		sb.append("Vol: " + player.getVolume() + " | ");
		sb.append("Pause: " + convertToEmoji(player.isPaused()) + " | ");
		sb.append("Repeat Song: " + convertToEmoji(queue.isRepeating()) + " | ");
		sb.append("Repeat Queue: " + convertToEmoji(queue.isLooping()));
		return sb.toString();
	}
	
	private static String convertToEmoji(boolean bool) {
		int codepoint = bool ? 0x2705 : 0x274C;
		return new String(Character.toChars(codepoint));
	}
	
	private void currentUrl(MessageReactionAddEvent event) {
		CircularDeque queue = getMusicBot().getPlaylist(getGuild());
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
		if (state.getChannel() == null) 
			println("%s You should be in a voice channel before you ask me to join =v.");
		else
			getMusicBot().connect(state.getChannel());
	}
	
	private String printCurrent() {
		CircularDeque queue = getMusicBot().getPlaylist(getGuild());
		AudioTrack track = queue.get(queue.getCurrent());
		return String.format("%d. %s (`%s`/%s) | Total Songs = `%s`", 
				queue.getCurrent(),
				track.getInfo().title, 
				StringLib.millisToTime(track.getPosition()), 
				StringLib.millisToTime(track.getDuration()),
				queue.size());
	}
	
	private void createCurrent() {
		getChannel().sendMessage(printCurrent())
				.queue(message -> 
					new ReactionsHandler(getBot())
							.handle(0x2747, reaction -> message.editMessage(printCurrent()).queue())
							.accept(message));
	}
	
	private void listLocal(String input) {
		Path start = Paths.get(StringLib.MUSIC_PATH);
		MusicFilesVisitor visitor = new MusicFilesVisitor(input, this);
		try {
			Files.walkFileTree(start, visitor);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
			println("Failed to list local dir: "+e.getMessage());
			return;
		}
		getChannel().sendMessage("Loading...")
			.queue(new PagedHandler<>(getBot(), visitor::getFound));
	}
	
	private static class MusicFilesVisitor implements FileVisitor<Path> {
		private String input;
		private ListTracks command;
		private List<String> found; 
		
		public MusicFilesVisitor(String input, ListTracks command) {
			this.input = input;
			this.command = command;
			found = new ArrayList<>();
		}
		
		public List<String> getFound() {
			return found;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			String path = file.toAbsolutePath().toString();
			if (StringLib.matchSimplified(path, input) && path.toLowerCase().endsWith("mp3")) 
				found.add(StringLib.obfuscateMusicFolder(path));
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			command.getLogger().error(exc.getMessage(), exc);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}
		
		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}
		
	}
}
