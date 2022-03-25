package commands.level.normal;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Restarter extends DiscordCommand {
	public static final AtomicBoolean FLAG = new AtomicBoolean(false);
	private static final Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	private static final String updater = "compile.bat";
	
	public Restarter(UserBot bot, Message message) {
		super(bot, message, Command.RESTART.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"# Args",
			"-n or --now\tMakes me shutdown faster",
			"-l or --log\tMakes me print update log",
			"Makes me update and restart.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-l", "--log")) {
			println(getUpdateLog());
			return;
		}
		printIndependently("Restarting..");
		bot.delay(2000);	// give 2s for message request to send
		getLogger().info("Restarting..");
		FLAG.set(true);		// set flag so exit code is changed on exit
		bot.kill(hasArgs("-n", "--now"));
	}
	
	public static int update() throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder()
				.inheritIO()
				.command(updater)
				.directory(path.toFile());
		Process p = pb.start();
		return p.waitFor();
	}
	
	public static String getUpdateLog() throws IOException {
		String workingDir = System.getProperty("user.dir");
		Path log = Paths.get(workingDir+"/logs/shadowjar-build.log").normalize();
		StringBuilder sb = new StringBuilder();
		Files.lines(log)
			.map((line) -> sb.append(line+System.lineSeparator()));
		return sb.toString();
	}
}