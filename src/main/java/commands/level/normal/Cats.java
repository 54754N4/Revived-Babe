package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.CatFactsResults;
import net.dv8tion.jda.api.entities.Message;

public class Cats extends DiscordCommand {
	public static final String FACTS_API = "https://cat-fact.herokuapp.com/facts";

	public Cats(UserBot bot, Message message) {
		super(bot, message, Command.CATS.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", "For all cat fact lovers. Rawr~");
	}

	@Override
	protected void execute(String input) throws Exception {
		CatFactsResults results = restRequest(CatFactsResults.class, FACTS_API);
		int total = results.all.length;
		println(results.all[rand.nextInt(total)].text);
	}
}
