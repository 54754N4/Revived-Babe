package commands.level.admin;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.messages.ReactionsHandler;
import net.dv8tion.jda.api.entities.Message;

public class Test extends DiscordCommand {
	public Test(UserBot bot, Message message) {
		super(bot, message, Command.TEST.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "Just used for testing");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("--react"))
			getChannel().sendMessage("Testing")
				.queue(new ReactionsHandler(getBot())
					.handle("zero", reaction -> printlnIndependently("I am custom"))
					.handle("\u0030\uFE0F\u20E3", reaction -> printlnIndependently("I am extended unicode native")));
		if (hasArgs("--api")) {
			println("VRSS getenv: %s", System.getenv("VOICE_RSS_API"));
			println("VRSS getProperty: %s", System.getenv("VOICE_RSS_API"));
			println("Babe: %s", System.getenv("BABE_BOT"));
		}
	}
}
