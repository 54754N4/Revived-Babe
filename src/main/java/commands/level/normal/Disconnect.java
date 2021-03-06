package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class Disconnect extends DiscordCommand {

	public Disconnect(UserBot bot, Message message) {
		super(bot, message, Command.DISCONNECT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<mentions>", "Makes me disconnect users (has to be in a server).");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (!fromGuild()) 
			println("Cannot retrieve user voice state outside of the server `#DiscordLimitation`");
		else if (mentioned.members.size() == 0)
			println("You need to mention users to disconnect.");
		else 
			for (Member member : mentioned.members) 
				getGuild().kickVoiceMember(member)
					.queue(null, e -> println("%s is not in a voice channel", member.getEffectiveName()));
	}
}
