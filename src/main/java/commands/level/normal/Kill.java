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
			int num = Integer.parseInt(input.trim())-1;
			String mention = Bot.Slaves.getBot(num).getAsMention();
			Bot slave = Bot.Slaves.get(num);
			getLogger().info("Killing {} ..", slave);
			Bot.Slaves.killSlave(slave, now);
			println("Killed %s.", mention);
		} else if (input.startsWith("echo")) {
			String mention = Bot.getEcho().getAsMention();
			Bot.killEcho(now);
			println("Killed %s.", mention);
		} else
			println("I need an integer or name.");
	}
}
