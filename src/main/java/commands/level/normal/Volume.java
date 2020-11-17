package commands.level.normal;

import bot.model.MusicBot;
import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Volume extends DiscordCommand {
	public static final int MIN = 0, MAX = 200;

	public Volume(UserBot bot, Message message) {
		super(bot, message, Command.VOLUME.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("[<number>]", "If you give me a number, I set it. Otherwise I print current.");
	}

	@Override
	protected void execute(String input) throws Exception {
		MusicBot bot = getMusicBot();
		if (input.trim().equals(""))
			println("Current volume is %d", bot.getVolume(guild));
		else if (!StringLib.isInteger(input)) 
			println("I only take integers as argument/parameter.");
		else {
			int vol = Integer.parseInt(input);
			if (vol < MIN || vol > MAX)
				println("Volume cannot be outside the range of [0, 200]%");
			else {
				bot.setVolume(guild, vol);
				println("Set volume to %d", vol);
			}
		}
	}

}
