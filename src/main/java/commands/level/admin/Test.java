package commands.level.admin;

import bot.model.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.messages.PagedHandler;
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
			channel.sendMessage("Testing")
				.queue(new ReactionsHandler()
					.handle("zero", reaction -> printlnIndependently("I am custom"))
					.handle("\u0030\uFE0F\u20E3", reaction -> printlnIndependently("I am extended unicode native")));
		if (hasArgs("--page")) {
			channel.sendMessage("Loading..")
				.queue(new PagedHandler<>(getMusicBot().getPlaylist(guild)));
		}
	}
}
