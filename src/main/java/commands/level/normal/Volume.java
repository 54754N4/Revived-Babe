package commands.level.normal;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Volume extends DiscordCommand {
	public static final int MIN = 0, MAX = 200;

	public Volume(UserBot bot, Message message) {
		super(bot, message, Command.VOLUME.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("[<number>]", "If you give me a number, I set it. Otherwise I print current.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (input.trim().equals(""))
			printCurrentVolume();
		else if (input.startsWith("+") || input.startsWith("-"))
			handleRelative(input);
		else if (!StringLib.isInteger(input)) 
			handleText(input);
		else 
			handleInt(Integer.parseInt(input));
	}
	
	private void handleRelative(String input) {
		int volume = getMusicBot().getVolume(guild),
			dv = Integer.parseInt(input.substring(1));
		getMusicBot().setVolume(guild, volume + dv);
	}

	private void printCurrentVolume() {
		println("Current volume is %d", getMusicBot().getVolume(guild));
	}

	private void handleInt(int vol) {
		if (vol < MIN || vol > MAX)
			println("Volume cannot be outside the range of [0, 200]%");
		else {
			getMusicBot().setVolume(guild, vol);
			println("Set volume to %d", vol);
		}
	}
	
	private void handleText(String input) {
		MusicBot bot = getMusicBot();
		if (input.matches("[\\+-]+")) {
			for (char c : input.toCharArray())
				if (c == '+')
					bot.increaseVolume(guild);
				else
					bot.decreaseVolume(guild);
		} else
			println("I only take integers as argument/parameter or +/-");
	}
}
