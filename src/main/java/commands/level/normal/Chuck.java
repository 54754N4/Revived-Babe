package commands.level.normal;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import json.ChuckNorrisResult;
import net.dv8tion.jda.api.entities.Message;

public class Chuck extends DiscordCommand {
	private static final String API_CALL = "https://api.chucknorris.io/jokes/random";
	
	public Chuck(UserBot bot, Message message) {
		super(bot, message, Command.CHUCK_NORRIS.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", "I give you the best Chuck Norris jokes.");
	}

	@Override
	protected void execute(String input) throws Exception {
		ChuckNorrisResult result = restRequest(API_CALL, ChuckNorrisResult.class);
		println(result.value);
	}
}
