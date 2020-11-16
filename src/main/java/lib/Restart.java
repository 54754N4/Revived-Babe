package lib;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Restart {
	public static final AtomicBoolean flag = new AtomicBoolean(false);

	private static final Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	private static final String starter = "start-bot.bat", updater = "compile.bat";
	private static final Logger logger = LoggerFactory.getLogger(Restart.class);
		
	public static int update() throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder()
				.inheritIO()
				.command(updater)
				.directory(path.toFile());
		Process p = pb.start();
		return p.waitFor();
	}

	public static void now() {
		logger.info("Adding restart hook now.");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ProcessBuilder pb = new ProcessBuilder()
						.inheritIO()						// pass back std.out and err to initial process that launched us
						.command(starter)
						.directory(path.toFile());
				logger.info("Restart Hook: Initiating restart.");
				try { pb.start(); } 
				catch (IOException e) {
					logger.error("Restart Hook: Error during restart", e);
				}
			}
		});
	}
}
