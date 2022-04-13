package commands.hierarchy;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import bot.hierarchy.UserBot;
import lambda.StatusUpdater;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

public abstract class PrintCommand extends Command {
	public static final int CODEBLOCK_LINE_MAX = 70, DEFAULT_PREV_MSGS = 2;
	
	public PrintCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
	}
	
	/* help building + argument parsing*/
	
	protected String helpBuilder(String args, String... lines) {
		StringBuilder sb = new StringBuilder();
		sb.append("#"+Arrays.toString(names)+"\n").append("Usage: <name> "+args+"\n");
		String output;
		for (String line: lines) {
			output = line;
			if (line.startsWith("[") || line.startsWith("<")) 
				output = "Usage: <name> " + output;
			else if (!line.startsWith("#") && !line.startsWith("Usage")) 
				output = "\t" + output;
			sb.append(output+"\n");
		}
		return markdown(sb.toString());
	}
	
	/* Convenience methods */
	
	public static String markdown(String input) {
		return "```markdown%n"+input+"%n```";
	}
	
	public static String inline(String input) {
		return "`"+input+"`";
	}
	
	public static String codeBlock(String input) {
		return codeBlock("", input);
	}
	
	public static String codeBlock(String language, String input) {
		return new StringBuilder()
			.append("```")
			.append(language+"\n")
			.append(input+"\n")
			.append("```")
			.toString();
	}
	
	private String format(String format, Object... args) {
		return String.format(format, args);
	}
	
	public void println() {
		print("%n");
	}
	
	public void print(String format, Object... args) { 
		getStdout().append(format(format, args));
	}
	
	public void println(String format, Object... args) {
		print(format+"%n", args);
	}
	
	public void printlnIndependently() {
		printIndependently("%n");
	}
	
	public void printIndependently(String format, Object... args) {
		if (format.equals("")) return;
		getChannel().sendMessage(format(format, args))
			.queue();
	}
	
	public void printlnIndependently(String format, Object... args) {
		printIndependently(format+"%n", args);
	}
	
	public void printCentered(String text) {
		print(StringLib.center(text, CODEBLOCK_LINE_MAX));
	}
	
	public void printlnCentered(String text) {
		println(StringLib.center(text, CODEBLOCK_LINE_MAX));
	}
	
	protected <T> void printItems(Collection<T> list) {
		printItems(list.toArray());
	}
	
	protected <T> void printItemsIndexed(Collection<T> list) {
		printItemsIndexed(list.toArray());
	}
	
	protected <T> void printItems(T[] list) {
		for (T item : list) print(markdown("%s"), item.toString());
	}
	
	protected <T> void printItemsIndexed(T[] list) {
		int i = 0;
		for (T item : list) print(markdown("%d.\t%s"), i++, item.toString());
	}
	
	protected <K, V> void printMap(Map<K, V> map) {
		for (Entry<K, V> entry : map.entrySet()) 
			print(markdown("%s = %s"), entry.getKey(), entry.getValue());
	}
	
	protected void printBlock(Collection<String> lines) {
		println("```");
		lines.forEach(this::println);
		println("```");
	}
	
	protected RestAction<Void> destructibleMessage(String message) {
		return destructibleMessage(message, 5);
	}
	
	protected RestAction<Void> destructibleMessage(String message, long seconds) {
		return getChannel().sendMessage(message)
				.delay(Duration.ofSeconds(seconds))
				.flatMap(Message::delete);
	}

	protected StatusUpdater getPrinter() {
		return str -> println(str);
	}
}
