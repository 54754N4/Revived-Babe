package commands.level.normal;

import java.io.IOException;
import java.net.MalformedURLException;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.hierarchy.RestCommand;
import commands.name.Command;
import json.FoodRecipeResults;
import json.FoodRecipeResults.Result;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Recipe extends DiscordCommand {
	public static final String API_FORMAT = "http://www.recipepuppy.com/api/?%s%s%s";
	
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
	public void execute(String input) throws Exception {
		String ingredients = hasArgs("--ingredients") ? getParams().getNamed().get("--ingredients") : "",
				search = hasArgs("--recipe") ? getParams().getNamed().get("--recipe") : "",
				page = hasArgs("--page") ? getParams().getNamed().get("--page") : "";
		FoodRecipeResults response = getRecipes(ingredients, search, page);
		for (Result result : response.results) 
			getChannel().sendMessageEmbeds(buildEmbed(result).build()).queue();
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
		return RestCommand.rest(FoodRecipeResults.class, API_FORMAT, ingredients, search, page);
	} 
}
