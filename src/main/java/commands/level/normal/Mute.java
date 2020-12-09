package commands.level.normal;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class Mute extends DiscordCommand {

	public Mute(UserBot bot, Message message) {
		super(bot, message, Command.MUTE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("[<mentions>]", "If you mention people I'll toggle their muted-state, otherwise I'll toggle mine.");
	}

	@Override
	protected void execute(String input) throws Exception {
		MusicBot bot = getMusicBot();
		if (mentioned.members.size() != 0)
			for (Member member : mentioned.members)
				bot.toggleMute(member);
		else 
			bot.toggleMute(guild);
	}

}
