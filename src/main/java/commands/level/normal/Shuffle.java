package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Shuffle extends DiscordCommand {

	public Shuffle(UserBot bot, Message message) {
		super(bot, message, Command.SHUFFLE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Makes me shuffle the playlist");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (!fromMusicBot()) {
			println("This command only works on music bots");
		}
		getMusicBot().getScheduler(guild).shuffle();
		println("Shuffled queue.");
	}

}
