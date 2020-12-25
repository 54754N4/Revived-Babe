package commands.level.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.model.Invoker;
import commands.name.Command;
import lib.scrape.Github;
import lib.scrape.Github.Dependency;
import lib.xml.TestReportsParser;
import lib.xml.XMLHandler;
import lib.xml.XMLHandler.Action;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Update extends DiscordCommand {
	public static final String TEST_RESULTS_DIR = "build/test-results/test";
	
	public Update(UserBot bot, Message message) {
		super(bot, message, Command.UPDATE.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("", 
			"-cmds or --commands\tMakes me re-discover newly created commands (without restart)",
			"-t or --tests\tMakes me print build test results", 	// TODO
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
		else if (hasArgs("-t", "--tests"))
			handleTests();
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
	
	private void handleTests() throws ParserConfigurationException, SAXException, IOException {
		String[] reports = Paths.get(TEST_RESULTS_DIR)
				.toFile()
				.list(Update::xmlOnly);
		for (String report : reports) {
			final EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Report for "+report);
			TestReportsParser.parse(new File(TEST_RESULTS_DIR+"/"+report), testHandler(embed));
			channel.sendMessage(embed.build()).queue();
		}
	}
	
	public static DefaultHandler testHandler(final EmbedBuilder embed) {
		return new XMLHandler.Builder()
			.add(new Action.Builder("testsuite")
				.handle("time", time -> embed.addField("SuiteTime", time, true))
				.handle("timestamp", timestamp -> embed.addField("Timestamp", timestamp, true))
				.handle("errors", errors -> embed.addField("Errors", errors, true))
				.handle("failures", failures -> embed.addField("Failures", failures, true))
				.handle("skipped", skipped -> embed.addField("Skipped", skipped, true).addBlankField(false))
				.handle("tests", tests -> embed.addField("Tests", tests, true))
				.build())
			.add(new Action.Builder("testcase")
				.handle("time", time -> embed.addField("Time", time, true))
				.handle("name", name -> embed.addField("Name", name, true))
				.handle("classname", classname -> embed.addField("Class Name", classname, true))
				.build())
			.build();
	}
	
	public static boolean xmlOnly(File dir, String name) {
		return name.endsWith(".xml");
	}
}
