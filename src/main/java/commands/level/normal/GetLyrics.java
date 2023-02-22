package commands.level.normal;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.PrintBooster;
import lib.lyrics.Lyrics;
import lib.lyrics.LyricsClient;
import lib.messages.PagedEmbedHandler;
import net.dv8tion.jda.api.entities.Message;

public class GetLyrics extends DiscordCommand {
	public static final LyricsClient CLIENT = new LyricsClient();
	public static final long TIMEOUT = 5000;

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
		final Lyrics lyrics;
		try {
			lyrics = CLIENT.getLyrics(input).get(TIMEOUT, TimeUnit.SECONDS);
		} catch (Exception e) {
			println("Failed to retrieve lyrics with error: %s", e.getMessage());
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
}
