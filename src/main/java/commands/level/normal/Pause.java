package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class Pause extends DiscordCommand {
	public Pause(UserBot bot, Message message) {
		super(bot, message, Command.PAUSE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Toggles between pause and unpause.");
	}

	@Override
	public void execute(String input) throws Exception {
		Guild guild = getGuild();
		println("Toggled pause state to %b", getMusicBot().togglePause(guild).isPaused(guild));
	}
}
