package commands.level.normal.role;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class Eval extends DiscordCommand {
	private static final ScriptEngineManager mgr = new ScriptEngineManager();
    private static final ScriptEngine js = mgr.getEngineByName("JavaScript");
	
	public Eval(UserBot bot, Message message) {
		super(bot, message, Command.EVAL.names);
		allowRole("Knights");
	}

	@Override
	public String helpMessage() {
		return helpBuilder("",
			"Makes me eval javascript code through a JS interpreter");
	}

	@Override
	protected void execute(String input) throws Exception {
		println("%s", javascript(input));
	}

	public static Object javascript(String input) throws ScriptException {
		return js.eval(input);
	}
}
