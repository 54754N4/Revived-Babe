package commands.level.admin;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.model.Invoker;
import net.dv8tion.jda.api.entities.Message;

public class Update extends DiscordCommand {

	public Update(UserBot bot, Message message) {
		super(bot, message, "update", "upd");
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", 
			"-cmds or --commands\t Makes me re-discover newly created commands (without restart)");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-cmds", "--commands"))
			Invoker.Reflector.update();
	}

}
