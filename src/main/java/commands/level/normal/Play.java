package commands.level.normal;

import java.util.concurrent.ExecutionException;

import bot.model.MusicBot;
import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Play extends DiscordCommand {
	public Play(UserBot bot, Message message) {
		super(bot, message, Command.PLAY.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder(	"[args] <index>",
				"Plays the song referenced by index from the queue.",
				"[args] <URI or keyword>",
				"URI can be either keywords, a file path (found using search command) or a URL.",
				"# Args",
//				"-p or --paged\tmakes me print search results paginated",			// TO-DO LATER
				"-v or --verbose\tmakes me print the songs being queued",
				"-pl or --playlist\t retrieves all songs of a playlist",
				"-sc or --soundcloud\t  searches keywords using SoundCloud",
				"-yt or --youtube\t\t searches keywords using YouTube",
				"--count=V\t\t\t\tretrieves V tracks from playlists (URLs) or keyword searches",
				"-n or --next\t\t\t adds new song next in the current queue",
				"-t or --top\t\t\t  adds new song the top of the current queue");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (input.equals(""))
			return;
		for (String term : input.split(","))
			play(term);
	}
	
	private void play(String input) throws NumberFormatException, InterruptedException, ExecutionException {
		MusicBot bot = getMusicBot();
		if (StringLib.isInteger(input)) {
			int index = Integer.parseInt(input),
				max = bot.getScheduler(guild).getQueue().size(); 
			if (max == 0) 
				println("Queue is currently empty..");
			else if (index>max || index<0) 
				println("Number currently can't be outside the range of [0,%d].", max-1);
			else 
				println("Loading song #%d", bot.play(guild, index));
			return;
		}
		if (StringLib.isKeyword(input)) {
			if (hasArgs("-sc", "--soundcloud"))
				input = SearchPrefix.SOUNDCLOUD.prefix(input);
			else 
				input = SearchPrefix.YOUTUBE.prefix(input);
		}
		getLogger().info("Loading track(s) from: {}", input);
		bot.play(guild, 
				input, 
				hasArgs("--top", "-t"), 
				hasArgs("--next", "-n"), 
				hasArgs("--count") ? Integer.parseInt(params.named.get("--count")) : 1, 
				hasArgs("-pl", "--playlist"), 
				hasArgs("-v", "--verbose") ? getPrinter() : null)
			.get();
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
