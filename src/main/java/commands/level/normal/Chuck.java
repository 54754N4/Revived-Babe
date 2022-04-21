package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.ChuckNorrisResult;
import net.dv8tion.jda.api.entities.Message;

public class Chuck extends DiscordCommand {
	public static final String API_CALL = "https://api.chucknorris.io/jokes/random";
	
	public Chuck(UserBot bot, Message message) {
		super(bot, message, Command.CHUCK_NORRIS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "I give you the best Chuck Norris jokes.");
	}

	@Override
	protected void execute(String input) throws Exception {
		ChuckNorrisResult result = restRequest(ChuckNorrisResult.class, API_CALL);
		println(result.value);
	}
}
