package commands.level.normal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import com.google.gson.Gson;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import json.cat.facts.Results;
import lib.HTTP;
import net.dv8tion.jda.api.entities.Message;

public class Cats extends DiscordCommand {
	public static final String CAT_FACTS_API = "https://cat-fact.herokuapp.com/facts";
	private static Random rand = new Random();
	private static Gson gson = new Gson();
	private static Results results;		// lazy loading for attributes that can throw

	public Cats(UserBot bot, Message message) {
		super(bot, message, Command.CATS.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", 
			"-f or --facts\tretrieves random cat fact",
			"For all cat lovers. Rawr~ <3");
	}

	@Override
	protected void execute(String input) throws Exception {
		Results results = getFacts();
		int total = results.all.length;
		println(results.all[rand.nextInt(total)].text);
	}

	public static Results getFacts() throws MalformedURLException, IOException {
		if (results != null)
			return results;
		try (	// try-with-resources to auto-close close-able resources
			HTTP.RequestBuilder builder = new HTTP.RequestBuilder(CAT_FACTS_API);
			HTTP.ResponseHandler handler = new HTTP.ResponseHandler(builder.build());
		) {
			return results = gson.fromJson(handler.getResponse(), Results.class);
		}
	}
}
