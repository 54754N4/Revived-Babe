package commands.level.normal;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import json.FoodImageResult;
import net.dv8tion.jda.api.entities.Message;

public class Food extends DiscordCommand {
	public static final String API_CALL = "https://foodish-api.herokuapp.com/api/";
	
	public Food(UserBot bot, Message message) {
		super(bot, message, Command.FOOD.names);
	}
	
	@Override
	public String helpMessage() {
		return helpBuilder("", "Feast your eyes.");
	}

	@Override
	public void execute(String input) throws Exception {
		FoodImageResult result = restRequest(FoodImageResult.class, API_CALL);
		println(result.image);
	}
}
