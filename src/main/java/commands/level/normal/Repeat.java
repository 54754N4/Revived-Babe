package commands.level.normal;

import bot.hierarchy.MusicBot;
import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class Repeat extends DiscordCommand {
	public Repeat(UserBot bot, Message message) {
		super(bot, message, Command.REPEAT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
				"-q or --queue\tToggles queue repeat state",
				"-t or --track\tToggles current track repeat state",
				"Repeats either the song or the queue, no arguments makes me just display current status of each.");
	}

	@Override
	public void execute(String input) throws Exception {
		MusicBot bot = getMusicBot();
		Guild guild = getGuild();
		if (hasArgs("-q", "--queue")) {
			bot.toggleLooping(guild);
			println("Toggled looping queue to %b", bot.isLooping(guild));
		} else if (hasArgs("-t", "--track")) {
			bot.toggleRepeating(guild);
			println("Toggled song repeating to %b", bot.isRepeating(guild));
		} else
			println("Song repeat state is : %b\nQueue repeat state is : %b", bot.isRepeating(guild), bot.isLooping(guild));
	}
}
