package commands.level.normal;

import bot.model.Bot;
import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Kill extends DiscordCommand {

	public Kill(UserBot bot, Message message) {
		super(bot, message, Command.KILL.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<number>", 
			"Kill bot specified with number.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (StringLib.isInteger(input)) {
			Bot slave = Bot.Slaves.get(Integer.parseInt(input.trim())-1);
			getLogger().info("Killing {} ..", slave);
			Bot.Slaves.killSlave(slave);
		} else {
			println("I need an integer.");
		}
	}

}
