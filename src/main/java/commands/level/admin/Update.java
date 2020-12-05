package commands.level.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import bot.model.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.model.Invoker;
import commands.name.Command;
import lib.scrape.Github;
import lib.scrape.Github.Dependency;
import net.dv8tion.jda.api.entities.Message;

public class Update extends DiscordCommand {
	public Update(UserBot bot, Message message) {
		super(bot, message, Command.UPDATE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"-cmds or --commands\t Makes me re-discover newly created commands (without restart)",
			"-g or --gradle\tChecks gradle for updates",
			"Used for updating specific things. No shit lol.");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-cmds", "--commands")) {
			Invoker.Reflector.update();
			println("Successfully updated commands list.");
		} else if (hasArgs("-g", "--gradle"))
			handleGradle();
		else 
			println("Please give me a parameter.");
	}

	private void handleGradle() throws IOException {
		String gradle = Files.readString(Paths.get("build.gradle"));
		Map<Dependency, String> versions = new ConcurrentHashMap<>();
		List<Dependency> updateable = new ArrayList<>();
		for (Dependency dependency : Github.Dependency.values()) {
			versions.put(dependency, dependency.latest.fetch());
			logger.info("Got version {} for {}", versions.get(dependency), dependency.name());
		}
		for (Entry<Dependency, String> entry : versions.entrySet())
			if (!gradle.contains(entry.getValue()))
				updateable.add(entry.getKey());
		if (updateable.size() == 0) {
			printlnIndependently("All up to date.");
			return;
		}
		final StringBuilder sb = new StringBuilder("You have to update : ");
		updateable.stream()
			.map(Dependency::name)
			.forEach(d -> sb.append(d+", "));
		printlnIndependently(sb.delete(sb.length()-2, sb.length()).toString());
	}
}
