package commands.level.normal;

import bot.model.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.Emoji;
import net.dv8tion.jda.api.entities.Message;

public class Ping extends DiscordCommand {

	public Ping(UserBot bot, Message message) {
		super(bot, message, Command.PING.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
			"Pings how far Discord server is from me "+Emoji.SPARKLING_HEART+".");
	}
	
	@Override
	public void execute(String command) {
		long ping = bot.getJDA().getGatewayPing();
		print("Discord server is **%d** _ms_ away from me.", ping);
		if (ping < 100) println(" So close ~~ "+Emoji.HEART_WITH_ARROW);
		else if (ping < 200) println(". That means I might stutter sometimes 3'=");
		else println(". Sooo far ~~~ "+Emoji.BROKEN_HEART+Emoji.BROKEN_HEART+Emoji.BROKEN_HEART);
	}

}
