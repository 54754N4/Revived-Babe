package commands.level.normal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		List<String> toggled = new ArrayList<>();
		MusicBot bot = getMusicBot();
		if (mentioned.members.size() != 0) {
			for (Member member : mentioned.members) {
				bot.toggleMute(member);
				toggled.add(member.getAsMention());
			}
		} else {
			bot.toggleMute(guild);
			toggled.add(bot.getAsMention());
		}
		println("Muted : %s", Arrays.toString(toggled.toArray()));
	}

}
