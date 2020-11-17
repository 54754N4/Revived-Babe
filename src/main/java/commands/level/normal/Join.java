package commands.level.normal;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Join extends DiscordCommand {

	public Join(UserBot bot, Message message) {
		super(bot, message, Command.JOIN.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("<channel>", "Makes me join your voice channel");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (message.getMember() == null || getGuild() == null) { 
			println("Sadly music commands only work in guilds/servers, not from private/group messages.");
			return;
		}
		VoiceChannel channel = message.getMember().getVoiceState().getChannel();
		if (channel == null) {
			println("You have to be in a voice channel so i can join =v..");
			return;
		}
		getMusicBot().connectTo(channel);
		println("Joined voice channel `%s`.", channel.getName());
	}

}
