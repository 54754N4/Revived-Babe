package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Message;

public class Join extends DiscordCommand {

	public Join(UserBot bot, Message message) {
		super(bot, message, Command.JOIN.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<channel>", "Makes me join your voice channel");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (getMessage().getMember() == null || getGuild() == null) { 
			println("Sadly music commands only work in guilds/servers, not from private/group messages.");
			return;
		}
		AudioChannel channel = getMessage().getMember().getVoiceState().getChannel();
		if (channel == null) {
			println("You have to be in a voice channel so i can join =v..");
			return;
		}
		getMusicBot().connect(channel);
		println("Joined voice channel `%s`.", channel.getName());
	}

}
