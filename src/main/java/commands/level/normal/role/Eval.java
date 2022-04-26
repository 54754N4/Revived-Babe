package commands.level.normal.role;

import java.util.stream.Stream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Eval extends DiscordCommand {
	private static final ScriptEngineManager mgr = new ScriptEngineManager();
    private static final ScriptEngine js = mgr.getEngineByName("JavaScript"),
    		py = mgr.getEngineByName("python");
	
	public Eval(UserBot bot, Message message) {
		super(bot, message, Command.EVAL.names);
		allowRole("Knights");
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
			"-js or --javascript\texecutes JS code",
			"-py or --python\texecutes python code",
			"-l or --list\tlists all scripting engines factories",
			"Makes me eval code through a specific interpreter");
	}

	@Override
	protected void execute(String input) throws Exception {
		if (hasArgs("-l", "--list"))
			printItems(factories());
		else if (hasArgs("-py", "--python"))
			println("%s", py.eval(input));
		else
			println("%s", js.eval(input));
	}
	
	private Stream<String> factories() {
		return mgr.getEngineFactories()
				.stream()
				.map(engine -> String.format(
						"Name: %s (%s)%nLanguage: %s (%s)", 
						engine.getEngineName(), 
						engine.getEngineVersion(), 
						engine.getLanguageName(), 
						engine.getLanguageVersion()));
	}
}
