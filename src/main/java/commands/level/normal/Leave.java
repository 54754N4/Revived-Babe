package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Leave extends DiscordCommand {

	public Leave(UserBot bot, Message message) {
		super(bot, message, Command.LEAVE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Makes me leave voice channel (if i'm in one ofc =v).");
	}

	@Override
	protected void execute(String input) throws Exception {
		print("Left %s", getMusicBot().disconnect(getGuild()).getName());
	}

}
