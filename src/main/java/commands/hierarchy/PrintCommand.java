package commands.hierarchy;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lambda.StatusUpdater;
import lib.StringLib;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

public interface PrintCommand extends ICommand {
	public static final int CODEBLOCK_LINE_MAX = 70, DEFAULT_PREV_MSGS = 2;
	
	/* help building + argument parsing*/
	
	default String helpBuilder(String args, String... lines) {
		StringBuilder sb = new StringBuilder();
		sb.append("#"+Arrays.toString(getNames())+"\n").append("Usage: <name> "+args+"\n");
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
	
	default void println() {
		print("%n");
	}
	
	default void print(String format, Object... args) { 
		getStdout().append(format(format, args));
	}
	
	default void println(String format, Object... args) {
		print(format+"%n", args);
	}
	
	default void printlnIndependently() {
		printIndependently("%n");
	}
	
	default void printIndependently(String format, Object... args) {
		if (format.equals("")) return;
		getChannel().sendMessage(format(format, args))
			.queue();
	}
	
	default void printlnIndependently(String format, Object... args) {
		printIndependently(format+"%n", args);
	}
	
	default void printCentered(String text) {
		print(StringLib.center(text, CODEBLOCK_LINE_MAX));
	}
	
	default void printlnCentered(String text) {
		println(StringLib.center(text, CODEBLOCK_LINE_MAX));
	}
	
	default <T> void printItems(Stream<T> stream) {
		printItems(stream.toArray());
	}
	
	default <T> void printItemsIndexed(Stream<T> stream) {
		printItemsIndexed(stream.toArray());
	}
	
	default <T> void printItems(Collection<T> list) {
		printItems(list.toArray());
	}
	
	default <T> void printItemsIndexed(Collection<T> list) {
		printItemsIndexed(list.toArray());
	}
	
	default <T> void printItems(T[] list, Predicate<T> condition, Function<T, String> action) {
		String printable;
		for (T item : list) {
			printable = item.toString();
			if (condition.test(item))
				printable = action.apply(item);
			print(markdown("%s"), printable);
		}
	}

	default <T> void printItemsIndexed(T[] list, BiPredicate<Integer, T> condition, BiFunction<Integer, T, String> action) {
		String printable;
		int i = 0;
		for (T item : list) {
			printable = item.toString();
			if (condition.test(i, item))
				printable = action.apply(i, item);
			print(markdown("%d.\t%s"), i++, printable);
		}
	}
	
	default <T> void printItems(T[] list) {
		for (T item : list) print(markdown("%s"), item.toString());
	}
	
	default <T> void printItemsIndexed(T[] list) {
		int i = 0;
		for (T item : list) print(markdown("%d.\t%s"), i++, item.toString());
	}
	
	default <K, V> void printMapFiltered(Map<K, V> map, String filter) {
		for (Entry<K, V> entry : map.entrySet())
			if (StringLib.matchSimplified(entry.getKey().toString(), filter)
				|| StringLib.matchSimplified(entry.getValue().toString(), filter))
				print(markdown("%s = %s"), entry.getKey(), entry.getValue());
	}
	
	default <K, V> void printMap(Map<K, V> map) {
		for (Entry<K, V> entry : map.entrySet()) 
			print(markdown("%s = %s"), entry.getKey(), entry.getValue());
	}
	
	default void printBlock(Collection<String> lines) {
		println("```");
		lines.forEach(this::println);
		println("```");
	}
	
	default RestAction<Void> destructibleMessage(String message) {
		return destructibleMessage(message, 5);
	}
	
	default RestAction<Void> destructibleMessage(String message, long seconds) {
		return getChannel().sendMessage(message)
				.delay(Duration.ofSeconds(seconds))
				.flatMap(Message::delete);
	}

	default StatusUpdater getPrinter() {
		return str -> println(str);
	}
	
	/* Convenience methods */
	
	private String format(String format, Object... args) {
		return String.format(format, args);
	}
	
	default String markdown(String input) {
		return Print.markdown(input);
	}
	
	default String inline(String input) {
		return Print.inline(input);
	}
	
	default String codeBlock(String input) {
		return Print.codeBlock(input);
	}
	
	default String codeBlock(String language, String input) {
		return Print.codeBlock(language, input);
	}
	
	/* Static alternatives in case commands use them in static methods */
	public static interface Print {
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
	}
}
