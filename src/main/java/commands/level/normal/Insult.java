package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.InsultsResult;
import net.dv8tion.jda.api.entities.Message;

public class Insult extends DiscordCommand {
	public static final String DEFAULT_LANG = "en", DEFAULT_TYPE = "json", 
		API_FORMAT = "https://evilinsult.com/generate_insult.php?lang=%s&type=%s";
	
	public Insult(UserBot bot, Message message) {
		super(bot, message, Command.INSULT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
				"--lang=L\twhere L is 'en' by default",
				"I insult.");
	}

	@Override
	protected void execute(String input) throws Exception {
		InsultsResult result = restRequest(
				API_FORMAT, 
				InsultsResult.class, 
				hasArgs("--lang") ? params.named.get("--lang") : DEFAULT_LANG, 
				DEFAULT_TYPE);
		println(result.insult);
	}
}
