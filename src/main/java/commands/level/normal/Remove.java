package commands.level.normal;

import java.util.Arrays;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.CircularDeque;
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
	}

	@Override
	public void execute(String input) {
		if (!fromMusicBot()) {
			println("Command only works on music bots.");
			return;
		}
		long guildId = getMessage().getGuild().getIdLong();
		CircularDeque queue = getMusicBot().getPlaylist(getGuild());
		int[] indices = Arrays.stream(input.split(SEPARATOR))
				.filter(StringLib::isInteger)
				.mapToInt(Integer::parseInt)	// parses + un-boxes to primitive type
				.filter(i -> i >= 0 && i < queue.size())
				.toArray();
		if (indices.length == 0) {
			println("No valid indices found");
			return;
		}
		print("Removed:%n");
		getMusicBot().remove(guildId, indices)	// returns removed tracks
			.map(Remove::prettyPrintTrack)
			.forEach(this::println);
	}
	
	private static String prettyPrintTrack(AudioTrack track) {
		return String.format("\t%s -> `%s`", track.getInfo().title, track.getInfo().uri);
	}
}
