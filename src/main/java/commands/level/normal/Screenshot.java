package commands.level.normal;

import java.io.File;

import org.openqa.selenium.By;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.scrape.Browser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.FileUpload;

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
	public void execute(String url) {
		try {
			Browser browser = Browser.getInstance().visit(url);
			File screenshot = hasArgs("--select") ? 
					browser.screenshotFileOf(By.cssSelector(getParams().getNamed().get("--select"))):
					browser.screenshotFullAsFile();
			getLogger().info(">>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<");
			getLogger().info(screenshot.getAbsolutePath());
			getChannel().sendFiles(FileUpload.fromData(screenshot)).complete();
			if (!screenshot.delete())
				println("Could not delete temporary screenshot file.");
		} catch (Exception e) {
			println("Could not access url.");
			getLogger().info(">>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<");
			getLogger().error("Coulnd't access url", e);
		}
	}
}
