package commands.level.normal;

import bot.model.UserBot;
import commands.model.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Highlight extends DiscordCommand {
	
	public Highlight(UserBot bot, Message msg) {
		super(bot, msg, Command.HIGHLIGHT.names);
	}
	
	@Override
	public String helpMessage() {
		return helpBuilder("[args] <input>",
			"Makes me re-paste as a code block.",
			"# Args",
			"--lang=L\twhere L is the language to use (e.g. AutoHotkey, Java, Python..)");
	}

	@Override
	public void execute(String command) {
		String language = "";
		if (hasArgs("--lang")) language = params.named.get("--lang");
		print(codeBlock(language, command));
	}
}