package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.LyricsOVHResult;
import lib.encode.Encoder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Lyrics extends DiscordCommand {
	private static final String API_FORMAT = "https://api.lyrics.ovh/v1/%s/%s";

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
		String[] split = input.split("-"), 
			treated = new String[2];
		treated[0] = Encoder.encodeURL(split[0] = split[0].trim());
		treated[1] = Encoder.encodeURL(split[1] = split[1].trim());
		LyricsOVHResult result = restRequest(LyricsOVHResult.class, API_FORMAT, treated[0], treated[1]);
		if (result.lyrics == null || result.lyrics.equals(""))
			println("Couldn't find lyrics");
		else 
			channel.sendMessageEmbeds(new EmbedBuilder()
				.setTitle(String.format("%s - %s", split[0], split[1]))
				.setDescription(fixSpacing(result.lyrics))
				.build())
			.queue();
	}

	private String fixSpacing(String lyrics) {
		lyrics = lyrics.replaceAll("\n{3,}", "PLACEHOLDER");
		lyrics = lyrics.replaceAll("\n{2,}", "\n");
		lyrics = lyrics.replace("PLACEHOLDER", "\n\n");
		return lyrics;
	}
}
