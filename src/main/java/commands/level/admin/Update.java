package commands.level.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.model.Invoker;
import commands.name.Command;
import lib.scrape.Dependency;
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

	private void handleGradle() throws Exception {
		Dependency.checkUpdates((name, latest, updated) -> 
			printlnIndependently(updated ? "%s already updated." : "%s needs to be updated to version : `%s`", name, latest));
	}
	
	private void handleTests() throws ParserConfigurationException, SAXException, IOException {
		String[] reports = Paths.get(TEST_RESULTS_DIR)
				.toFile()
				.list(Update::xmlOnly);
		for (String report : reports) {
			final EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Report for "+report);
			TestReportsParser.parse(new File(TEST_RESULTS_DIR+"/"+report), testHandler(embed));
			channel.sendMessageEmbeds(embed.build()).queue();
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
