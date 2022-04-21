package commands.level.normal;

import java.util.List;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.LyricsOVHResult;
import lib.PrintBooster;
import lib.encode.Encoder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Lyrics extends DiscordCommand {
	// https://lyricsovh.docs.apiary.io/#reference
	public static final String API_FORMAT = "https://api.lyrics.ovh/v1/%s/%s";

	public Lyrics(UserBot bot, Message message) {
		super(bot, message, Command.LYRICS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<artist> - <track>",
				"Retrieves lyrics.");
	}

	@Override
	protected void execute(String input) throws Exception {
		String[] split = input.split("-");
		if (split.length < 2) {
			println("Give me artist AND title name");
			return;
		}
		LyricsOVHResult result = getLyrics(split[0].trim(), split[1].trim());
		if (result == null || result.lyrics == null|| result.lyrics.equals(""))
			println("Couldn't find lyrics");
		else {
			List<String> lyrics = PrintBooster.splitEmbed(fixSpacing(result.lyrics));
			for (int i=0; i<lyrics.size(); i++)
				getChannel().sendMessageEmbeds(new EmbedBuilder()
					.setTitle(String.format("%s - %s (%d/%d)", split[0], split[1], i+1, lyrics.size()))
					.setDescription(lyrics.get(i))
					.build())
				.queue();
		}
	}
	
	private LyricsOVHResult getLyrics(String artist, String title) {
		try {
			return restRequest(LyricsOVHResult.class, API_FORMAT, Encoder.encodeURL(artist), Encoder.encodeURL(title));
		} catch (Exception e) {
			return null;
		}
	}

	public static String fixSpacing(String lyrics) {
		lyrics = lyrics.replaceAll("\n{3,}", "PLACEHOLDER");
		lyrics = lyrics.replaceAll("\n{2,}", "\n");
		lyrics = lyrics.replace("PLACEHOLDER", "\n\n");
		return lyrics;
	}
}
