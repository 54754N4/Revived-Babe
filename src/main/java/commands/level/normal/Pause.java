package commands.level.normal;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Pause extends DiscordCommand {
	public Pause(UserBot bot, Message message) {
		super(bot, message, Command.PAUSE.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", "Toggles between pause and unpause.");
	}

	@Override
	protected void execute(String input) throws Exception {
		println("Toggled pause state to %b", getMusicBot().togglePause(guild).isPaused(guild));
	}
}