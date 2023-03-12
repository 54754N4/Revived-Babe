package commands.level.normal;

import java.io.StringWriter;
import java.util.Map;

import javax.annotation.Nullable;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bot.hierarchy.UserBot;
import commands.hierarchy.DiscordCommand;
import commands.name.Command;
import net.dv8tion.jda.api.entities.Message;

public class JavaScript extends DiscordCommand {
	private static final ScriptEngineManager FACTORY = new ScriptEngineManager();
	private static final ScriptEngine JS_ENGINE = FACTORY.getEngineByName("graal.js");

	public JavaScript(UserBot bot, Message message) {
		super(bot, message, Command.JAVASCRIPT.names);
	}

	@Override
	public String helpMessage() {
		return helpBuilder("<js code>", 
			"Makes me execute JS code. You can pass in variables by just passing named parameters.", 
			"Example: ..js --count=3 --name=\"satsana\" while (count-->0) print('Hello '+name);");
	}

	@Override
	public void execute(String input) throws Exception {
		StringWriter output = new StringWriter();
		println("Return: %s", eval(input, getBindings(), output));
		println("%s", output.toString());
	}
	
	// Converts from named parameters (params with double hyphens '--')
	private final Bindings getBindings() {
		final Bindings bindings = JS_ENGINE.createBindings();
		getParams().getNamed().keySet().stream()
			.forEach(param -> bindings.put(param.replaceAll("--", ""), getParams().getNamed().get(param)));
		return bindings;
	}

	public static Object eval(String input, @Nullable Map<String, Object> bind, @Nullable StringWriter writer) throws ScriptException {
		final Bindings bindings = JS_ENGINE.createBindings();
		if (bind != null && bind.size() != 0) 
			bind.forEach(bindings::put);
		return eval(input, bindings, writer);
	}
	
	public static Object eval(String input, Bindings bindings, @Nullable StringWriter writer) throws ScriptException {
		if (writer != null)
			JS_ENGINE.getContext().setWriter(writer);
		return JS_ENGINE.eval(input, bindings);
	}
}
