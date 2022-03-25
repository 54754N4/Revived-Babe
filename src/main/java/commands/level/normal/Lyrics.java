package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.LyricsOVHResult;
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
		treated[0] = urlEncode(split[0] = split[0].trim());
		treated[1] = urlEncode(split[1] = split[1].trim());
		LyricsOVHResult result = restRequest(LyricsOVHResult.class, API_FORMAT, treated[0], treated[1]);
		if (result.lyrics == null || result.lyrics.equals(""))
			println("Couldn't find lyrics");
		else 
			channel.sendMessageEmbeds(new EmbedBuilder()
				.setTitle(String.format("%s - %s", split[0], split[1]))
				.setDescription(result.lyrics)
				.build())
			.queue();
	}

}
