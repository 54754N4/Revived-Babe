package commands.level.normal;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.model.ThreadsManager;
import commands.name.Command;
import lib.PrintBooster;
import lib.StringLib;
import lib.lyrics.Lyrics;
import lib.lyrics.LyricsClient;
import lib.messages.PagedEmbedHandler;
import net.dv8tion.jda.api.entities.Message;

public class GetLyrics extends DiscordCommand {
	public static final String[] SOURCES = {"A-Z Lyrics", "Genius", "MusixMatch", "LyricsFreak"};
	public static final String DEFAULT_SOURCE = SOURCES[0];
	public static final long TIMEOUT = 5;

	public GetLyrics(UserBot bot, Message message) {
		super(bot, message, Command.LYRICS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<search term>",
				"--timeout=T\twhere T is seconds to wait before stopping search",
				"Retrieves lyrics based on your search input (by default searches for 5s before timing out).");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (input.length() == 0) {
			println("Tell me what you want me to get lyrics for.");
			return;
		}
		long timeout = TIMEOUT;
		if (hasArgs("--timeout")) {
			String given = getParams().getNamed().get("--timeout");
			if (StringLib.isInteger(given))
				timeout = Long.parseLong(given);
			else {
				println("Timeout has to be a number");
				return;
			}
		}
		final Lyrics lyrics = tryAllSources(input, timeout);
		if (lyrics == null) {
			println("Failed to retrieve lyrics: scraping returned `null`.");
			return;
		}
		List<String> results = PrintBooster.splitEmbed(lyrics.getContent());
		getChannel().sendMessage("Loading...")
			.queue(new PagedEmbedHandler<String>(getBot(), () -> results)
					.setItemHandler((index, total, part, builder) -> 
						builder.setTitle(String.format("%s (%d/%d)", input, index+1, total), lyrics.getURL())
							.setDescription(part)
							.setAuthor(lyrics.getAuthor())
							.setFooter(lyrics.getSource())));
	}
	
	public static Lyrics tryAllSources(String input, long timeout) {
		LyricsClient client;
		Lyrics lyrics = null;
		for (String source : SOURCES) {
			client = new LyricsClient(source, ThreadsManager.POOL);
			try {
				lyrics = client.getLyrics(input).get(timeout, TimeUnit.SECONDS);
			} catch (Exception e) {
				continue;
			}
			if (lyrics == null || lyrics.getContent() == null)
				continue;
			break;
		}
		return lyrics;
	}
}
