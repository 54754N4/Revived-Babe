package commands.level.normal;

import bot.hierarchy.Bot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;

public class Kill extends DiscordCommand {

	public Kill(UserBot bot, Message message) {
		super(bot, message, Command.KILL.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<number or name>",
			"# Args",
			"-n or --now\tmakes the bot shutdown faster",
			"Kill bot specified with number or name.");
	}

	@Override
	protected void execute(String input) throws Exception {
		boolean now = hasArgs("-n", "--now");
		if (StringLib.isInteger(input)) {
			Bot slave = Bot.Slaves.get(Integer.parseInt(input.trim())-1);
			getLogger().info("Killing {} ..", slave);
			Bot.Slaves.killSlave(slave, now);
		} else if (input.startsWith("echo")) 
			Bot.killEcho(now);
		else {
			println("I need an integer or name.");
		}
	}

}
