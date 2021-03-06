package commands.level.normal;

import java.io.File;

import org.openqa.selenium.By;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.scrape.Browser;
import net.dv8tion.jda.api.entities.Message;

public class Screenshot extends DiscordCommand {

	public Screenshot(UserBot bot, Message message) {
		super(bot, message, Command.SCREENSHOT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<url>",
				"--select=S\twhere S is the specific CSS selector to use",
				"Takes a screenshot of a specified url. (For reference of all CSS selectors, check here : https://www.w3schools.com/cssref/css_selectors.asp)");
	}

	@Override
	protected void execute(String url) {
		try {
			Browser browser = Browser.getInstance().visit(url);
			File screenshot = hasArgs("--select") ? 
					browser.screenshotFileOf(By.cssSelector(params.named.get("--select"))):
					browser.screenshotFullAsFile();
			channel.sendFile(screenshot).queue();
			if (!screenshot.delete())
				logger.error("Could not delete temporary screenshot file.");
		} catch (Exception e) {
			println("Could not access page");
		}
	}
}
