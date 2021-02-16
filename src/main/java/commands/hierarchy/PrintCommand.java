package commands.hierarchy;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import audio.track.handlers.TrackLoadHandler.StatusUpdater;
import bot.hierarchy.UserBot;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

public abstract class PrintCommand extends Command {
	public static final int CODEBLOCK_LINE_MAX = 70, DEFAULT_PREV_MSGS = 2;
	
	public PrintCommand(UserBot bot, Message message, String[] names) {
		super(bot, message, names);
	}
	
	public static String markdown(String input) {
		return "```markdown\n"+input+"\n```";
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
	
	private String returnOrFormat(String format, Object... args) {
		return (args.length == 0) ? format : String.format(format, args);
	}
	
	public void println() {
		print("\n");
	}
	
	public void print(String format, Object... args) { 
		stdout.append(returnOrFormat(format, args));
	}
	
	public void println(String format, Object... args) {
		print(format+"\n", args);
	}
	
	public void printlnIndependently() {
		printIndependently("\n");
	}
	
	public void printIndependently(String format, Object... args) {
		if (format.equals("")) return;
		channel.sendMessage(returnOrFormat(format, args))
			.queue();
	}
	
	public void printlnIndependently(String format, Object... args) {
		printIndependently(format+"\n", args);
	}
	
	public void printCentered(String text) {
		print(StringLib.center(text, CODEBLOCK_LINE_MAX));
	}
	
	public void printlnCentered(String text) {
		println(StringLib.center(text, CODEBLOCK_LINE_MAX));
	}
	
	protected <T> void printItems(Collection<T> list) {
		for (T item : list) print(markdown(item.toString()));
	}
	
	protected <T> void printItemsIndexed(Collection<T> list) {
		int i = 0;
		for (T item : list) print(markdown(i+++".\t"+item.toString()));
	}
	
	protected <T> void printItems(T[] list) {
		for (T item : list) print(markdown(item.toString()));
	}
	
	protected <T> void printItemsIndexed(T[] list) {
		int i = 0;
		for (T item : list) print(markdown(i+++".\t"+item.toString()));
	}
	
	protected <K, V> void printMap(Map<K, V> map) {
		for (Entry<K, V> entry : map.entrySet()) 
			print(markdown(entry.getKey()+" = "+entry.getValue()));
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
		return channel.sendMessage(message)
				.delay(Duration.ofSeconds(seconds))
				.flatMap(Message::delete);
	}

	protected StatusUpdater getPrinter() {
		return str -> println(str);
	}
}
