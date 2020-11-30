package commands.level.normal;

import java.util.Arrays;

import bot.model.MusicBot;
import bot.model.UserBot;
import commands.model.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Speak extends DiscordCommand {
	public Speak(UserBot bot, Message message) {
		super(bot, message, Command.SPEAK.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<text>", 
			"-l or --list\tmakes me list voices",
			"--voice=V\twhere V stands for a valid voice",
			"Makes me text-to-speech. (Note: I have to be in a voice channel. Duh)");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-l", "--list")) {
			println("Voices from: %s", Arrays.toString(new String[] {"David", "Hazel", "Helena", "Hortense", "Zira"}));
			return;
		}
		MusicBot bot = getMusicBot();
		boolean connected = bot.isConnected(guild),
				authorConnected = message.getMember().getVoiceState().inVoiceChannel();
		if (!connected && !authorConnected) {
			println("I have to be in a voice channel to talk.. So you also have to be in one so I can join you lol..");
			return;
		} else if (!connected) 
			bot.connectTo(message.getMember().getVoiceState().getChannel());
		String voice = hasArgs("--voice") ? params.named.get("--voice") : "Hazel";
		bot.speak(guild, voice, input);
	}
}