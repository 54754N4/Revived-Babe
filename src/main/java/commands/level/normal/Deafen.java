package commands.level.normal;

import bot.model.MusicBot;
import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class Deafen extends DiscordCommand {
	public Deafen(UserBot bot, Message message) {
		super(bot, message, Command.DEAFEN.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("[<mentions>]", "If you mention people I'll toggle their deafened-state, otherwise I'll toggle mine.");
	}

	@Override
	protected void execute(String input) throws Exception {
		MusicBot bot = getMusicBot();
		if (mentioned.members.size() != 0)
			for (Member member : mentioned.members)
				bot.toggleDeafen(member);
		else 
			bot.toggleDeafen(guild);
	}
}