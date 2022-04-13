package commands.level.normal;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import lib.LineShiftBuffer;
import net.dv8tion.jda.api.entities.Message;

public class Log extends DiscordCommand {
	private static final String LOG_FILE = "./logs/current-babe.log",
			BUILD_LOG = "./logs/shadowjar-build.log";
	private static final int DEFAULT_TAIL = 5;

	public Log(UserBot bot, Message message) {
		super(bot, message, Command.Log.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
			"# Args",
			"-b or --build\tlists build log",
			"--lines=N\tmakes me print the last N lines of file",
			"By default I print 5 lines from the end of the current log, otherwise all the build log.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-b", "--build"))
			Files.readAllLines(Paths.get(BUILD_LOG))
				.forEach(this::println);
		else {
			int lines = hasArgs("--lines") ? Integer.parseInt(getParams().getNamed().get("--lines")) : DEFAULT_TAIL;
			List<String> tail = LineShiftBuffer.getTail(lines, new FileInputStream(Paths.get(LOG_FILE).toFile()));
			printBlock(tail);
		}
	}
}