package commands.level.normal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import audio.TrackScheduler;
import audio.track.handlers.PagedTracksHandler;
import audio.track.handlers.TrackLoadHandler;
import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.SpotifyLinkConverter;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public class Play extends DiscordCommand {
	
	public Play(UserBot bot, Message message) {
		super(bot, message, Command.PLAY.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder(	"[args] <index>",
				"Plays the song referenced by index from the queue.",
				"[args] <URI or keyword>",
				"URI can be either keywords, a file path (found using search command) or a URL.",
				"# Args",
				"-p or --paged\tmakes me print search results paginated",
				"-v or --verbose\tmakes me print the songs being queued",
				"-a or --all\t retrieves all songs retrieved (e.g. playlists)",
				"-sc or --soundcloud\t  searches keywords using SoundCloud",
				"-yt or --youtube\t\t searches keywords using YouTube",
				"--count=V\t\t\t\tretrieves V tracks from playlists (URLs) or keyword searches",
				"-n or --next\t\t\t adds new song next in the current queue",
				"-t or --top\t\t\t  adds new song the top of the current queue");
	}

	@Override
	public void execute(String input) throws Exception {
		if (input.equals(""))
			return;
		for (String term : input.split(","))
			play(term.trim());
	}
	
	private void play(String input) throws NumberFormatException, InterruptedException, ExecutionException {
		Guild guild = getGuild();
		MusicBot bot = getMusicBot();
		TrackScheduler scheduler = bot.getScheduler(guild);
		if (input.equals("")) {
			println(helpMessage());
			return;
		}
		if (!bot.isConnected(guild)) {
			AudioChannel audio = getMessage().getMember().getVoiceState().getChannel();
			if (audio == null) {
				println("Can't join voice channel, you're not in one");
				return;
			} else
				bot.connect(audio);
		}
		if (StringLib.isInteger(input)) {
			int index = Integer.parseInt(input),
				max = scheduler.getQueue().size();
			if (max == 0) 
				println("Queue is currently empty..");
			else if (index >= max || index < 0) 
				println("Number currently can't be outside the range of [0,%d].", max-1);
			else 
				println("Loading song #%d", bot.play(guild, index));
			return;
		}
		if (input.startsWith("~"))
			input = StringLib.deobfuscateMusicFolder(input);
		else if (StringLib.isKeyword(input)) {
			if (hasArgs("-sc", "--soundcloud"))
				input = SearchPrefix.SOUNDCLOUD.prefix(input);
			else
				input = SearchPrefix.YOUTUBE.prefix(input);
		}
		getLogger().info("Loading track(s) from: {}", input);
		TrackLoadHandler trackHandler = createTrackLoadHandler(scheduler);
		if (hasArgs("-p", "--paged")) {
			if (StringLib.isURL(input)) {
				println("`-p` and `--paged` is only listing search results. You can't use urls with this.");
				return;
			}
			PagedTracksHandler handler = new PagedTracksHandler(bot, scheduler, trackHandler, new ArrayList<>())
					.loopbackIndices();		// indices always between [0,9]
			bot.play(guild, input, handler).get();
			getChannel().sendMessage("Loading...").queue(handler.enableHandlerButtons());
		} else if (input.contains("open.spotify.com")) {
			List<String> songs = SpotifyLinkConverter.getInstance().convertPlaylist(input);
			if (songs.size() == 0) {
				println("Could not extract song names from spotify playlist `%s`", input);
				return;
			}
			for (String song : songs)
				play(song);
		} else
			bot.play(guild, input, trackHandler).get();
	}
	
	private TrackLoadHandler createTrackLoadHandler(TrackScheduler scheduler) {
		return new TrackLoadHandler.Builder(scheduler)
			.setTop(hasArgs("--top", "-t"))
			.setNext(hasArgs("--next", "-n"))
			.setCount(hasArgs("--count") ? Integer.parseInt(getParams().getNamed().get("--count")) : 1)
			.setPlaylist(hasArgs("-a", "--all"))
			.setStatusUpdater(hasArgs("-v", "--verbose") ? getPrinter() : getLogger()::info)
			.build();
	}
	
	public static enum SearchPrefix {
		YOUTUBE("ytsearch:"), SOUNDCLOUD("scsearch:");
		
		private String prefix;
		
		private SearchPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		public String prefix(String string) {
			return prefix + string;
		}
		
		public String toString() {
			return prefix;
		}
	};
}
