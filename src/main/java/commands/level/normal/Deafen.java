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

public class Deafen extends DiscordCommand {
	public Deafen(UserBot bot, Message message) {
		super(bot, message, Command.DEAFEN.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("[<mentions>]", "If you mention people I'll toggle their deafened-state, otherwise I'll toggle mine.");
	}

	@Override
	public void execute(String input) throws Exception {
		List<String> toggled = new ArrayList<>();
		MusicBot bot = getMusicBot();
		if (getMentions().getMembers().size() != 0) {
			for (Member member : getMentions().getMembers()) { 
				bot.toggleDeafen(member);
				toggled.add(member.getAsMention());
			}
		} else {
			toggled.add(bot.getAsMention());
			bot.toggleDeafen(getGuild());
		}
		println("Toggled deafened state of : %s", Arrays.toString(toggled.toArray()));
	}
}