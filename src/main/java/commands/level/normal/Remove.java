package commands.level.normal;

import java.util.Arrays;
import java.util.stream.IntStream;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Remove extends DiscordCommand {
	public static final String SEPARATOR = " "; 

	public Remove(UserBot bot, Message message) {
		super(bot, message, Command.REMOVE.names);
	}
	
	@Override
	public String helpMessage() {
		return helpBuilder("<indices>",
			"Deletes the songs specified as parameter, each index separated by spaces.");
//			"[-t|--tag] <pattern>",
//			"Deletes the tags that matched the given pattern.");
	}

	@Override
	protected void execute(String input) {
		if (!fromMusicBot()) {
			println("Command only works on music bots.");
			return;
		}
		IntStream indices = Arrays.stream(input.split(SEPARATOR))
				.filter(StringLib::isInteger)
				.mapToInt(Integer::parseInt);	// parses + un-boxes to primitive type
		getMusicBot().remove(message.getGuild().getIdLong(), indices);
	}
}
