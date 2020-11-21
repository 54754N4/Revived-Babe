package commands.level.admin;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.messages.ReactionsTracker;
import net.dv8tion.jda.api.entities.Message;

public class Test extends DiscordCommand {
	public Test(UserBot bot, Message message) {
		super(bot, message, Command.TEST.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", "Just used for testing");
	}

	@Override
	protected void execute(String input) throws Exception {
		channel.sendMessage("Testing")
			.queue(new ReactionsTracker()
					.handle("zero", reaction -> printlnIndependently("I am custom"))
					.handle(0x1F4AF, reaction -> printlnIndependently("I am native")));
	}
}
