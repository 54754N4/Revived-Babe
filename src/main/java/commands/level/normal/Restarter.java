package commands.level.normal;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bot.model.UserBot;
import commands.model.DiscordCommand;
import commands.name.Command;
import lib.Restart;
import net.dv8tion.jda.api.entities.Message;

public class Restarter extends DiscordCommand {

	public Restarter(UserBot bot, Message message) {
		super(bot, message, Command.RESTART.names);
	}

	@Override
	protected String helpMessage() {
		return helpBuilder("", 
			"# Args",
			"-f or --flag\tMakes me return current restart flag",
			"-u or --update\tMakes me update myself",
			"-c or --compile\tMakes me update myself",
			"-n or --now\tMakes me shutdown faster",
			"Makes me restart or update.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-f", "--flag")) {
			println("Restart flag : `"+Restart.flag.get()+"`.");
			return;
		}
		boolean update = hasArgs("-u", "--update", "-c", "--compile");
		int errorCode = 0;
		try {
			if (update) {
				printlnIndependently("Compiling..");
				errorCode = Restart.update();
			}
			if (errorCode != 0) {
				printlnIndependently("Compiling exit with status: %d", errorCode);
				printlnIndependently(getUpdateLog());
				return;
			}
			printIndependently("Restarting..");
			getLogger().info("Restarting..");
			bot.kill(hasArgs("-n", "--now"));
		} catch (IOException | InterruptedException e) {
			if (update)
				printlnIndependently(getUpdateLog());
			println("Error: "+e.getMessage());
			getLogger().error("Error occured while initiating restart", e); 
		}
		
	}
	
	public static String getUpdateLog() throws IOException {
		String root = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
		Path log = Paths.get(root+"\\shadowjar-build.log").normalize();
		StringBuilder sb = new StringBuilder();
		Files.lines(log)
			.map((line) -> sb.append(line+System.lineSeparator()));
		return sb.toString();
	}
}