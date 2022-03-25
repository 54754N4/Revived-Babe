package commands.level.normal;

import java.io.IOException;
import java.net.MalformedURLException;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.FoodRecipeResults;
import json.FoodRecipeResults.Result;
import lib.HTTP;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Recipe extends DiscordCommand {
	private static final String API_FORMAT = "http://www.recipepuppy.com/api/?%s%s%s";
	
	public Recipe(UserBot bot, Message message) {
		super(bot, message, Command.RECIPE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"--ingredients=I\twhere I is a comma-separated list of ingredients",
			"--recipe=R\tmakes me match search result recipes with R",
			"--page=P\twhere P is the page number of results",
			"Lets me do keyword search using ingredients and/or normal recipes search.");
	}

	@Override
	protected void execute(String input) throws Exception {
		String ingredients = hasArgs("--ingredients") ? params.named.get("--ingredients") : "",
				search = hasArgs("--recipe") ? params.named.get("--recipe") : "",
				page = hasArgs("--page") ? params.named.get("--page") : "";
		FoodRecipeResults response = getRecipes(ingredients, search, page);
		for (Result result : response.results) 
			channel.sendMessageEmbeds(buildEmbed(result).build()).queue();
	}
	
	public static EmbedBuilder buildEmbed(Result result) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(result.title);
		eb.addField("Ingredients:", result.ingredients, false);
		eb.setImage(result.href);
		eb.setThumbnail(result.thumbnail);
		return eb;
	}
	
	public static FoodRecipeResults getRecipes(String ingredients, String search, String page) throws MalformedURLException, IOException {
		if (!ingredients.equals("")) 
			ingredients = "i=" + ingredients + "&";
		if (!search.equals(""))
			search = "q=" + search + "&";
		page = page.equals("") ? "p=1" : "p=" + page;
		try (	// try-with-resources to auto-close close-able resources
			HTTP.RequestBuilder builder = new HTTP.RequestBuilder(String.format(API_FORMAT, ingredients, search, page));
			HTTP.ResponseHandler handler = new HTTP.ResponseHandler(builder.build());
		) {
			return gson.fromJson(handler.getResponse(), FoodRecipeResults.class);
		}	
	} 
}
