package commands.level.normal;

import audio.CircularDeque;
import bot.model.UserBot;
import commands.model.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Get extends DiscordCommand {

	public Get(UserBot bot, Message message) {
		super(bot, message, Command.GET.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<index>", "Give me index of song to get url from.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (!StringLib.isInteger(input)) {
			println("You need to give me an integer.");
			return;
		}
		CircularDeque queue = getMusicBot().getPlaylist(guild);
		if (queue.size() == 0) {
			println("No songs in queue.");
			return;
		}
		int index = Integer.parseInt(input);
		if (index < 0 && index >= queue.size()) {
			println("Index cannot be outside the range [0,%d]", queue.size());
			return;
		}
		println(queue.get(index).getInfo().uri);
	}

}
