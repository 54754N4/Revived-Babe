package lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Performance boost strategy takes 2 steps and is as follows : 
 * 1. if msg is less than MESSAGE_MAX -> it won't be touched 
 * 		else -> subdivide the message based on code blocks into tokens
 * 		if no code blocks or is still too big -> split token on newlines
 * 		if still too big -> split token on dots
 * 		if still too big -> split token on spaces
 * at this point, we suppose that after these 4 kinds of splits (worst case), 
 * tokens should all become valid but small discord messages.
 * 2. merge tokens as long as satisfy length < MESSAGE_MAX
 * 
 * This allows DiscordCommands to chain MessageActions (e.g. network I/O) 
 * using the tokens we give it since they're all the biggest we could
 * make from the command output. This greatly reduces the number of network 
 * calls we initiate to Discord, and directly translates to the bot replying 
 * faster no matter the output length.
 */
public abstract class PrintBooster {
	public static final int MESSAGE_MAX = Message.MAX_CONTENT_LENGTH,
			EMBED_MAX = MessageEmbed.DESCRIPTION_MAX_LENGTH;

	/* Splits normal discord messages */
	public static List<String> split(String string) {
		return Pipeline.of(PrintBooster::splitOnMarkdowns)
				.then(PrintBooster::splitOnNewlines)
				.then(PrintBooster::splitOnDots)
				.then(PrintBooster::splitOnSpaces)
				.then(PrintBooster::mergeForDiscord)
				.then(PrintBooster::filterEmpty)
				.apply(string);
	}
	
	/* Splits discord messages based on embed message descriptions max size */
	public static List<String> splitEmbed(String message) {
		if (message.length() < EMBED_MAX)
			return Arrays.asList(message);
		String space = " ";
		List<String> result = new ArrayList<>();
		String[] words = message.split(space);
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			if (sb.length() + word.length() + space.length() < EMBED_MAX)
				sb.append(word).append(space);
			else {
				result.add(sb.toString());
				sb.delete(0, sb.length());
				sb.append(word).append(space);
			}
		}
		if (sb.length() != 0)
			result.add(sb.toString());
		return result;
	}
	
	private static Markdown[] findMarkdowns(String sb) {
		List<Markdown> markdowns = new ArrayList<>();
		int start = -1, end = -1;
		start = sb.indexOf("```markdown");
		end = sb.indexOf("```", start+10)+3;				// +10 to skip start's match as well
															// +3 to skip end's match
		while (start != -1 && end != -1) {
			markdowns.add(new Markdown(start, end));
			start = sb.indexOf("```markdown", end); 	
			end = sb.indexOf("```", start+10)+3;			
		}  
		return markdowns.toArray(new Markdown[markdowns.size()]);
	}
	
	private static List<String> split(String target, String word) {
		List<String> smaller = new ArrayList<>();
		smaller.addAll(Arrays.asList(word.split(target)));
		int i = 0;
		for (String s : smaller) 
			smaller.set(i++, s+target);	// since we dont want to consume the target
		return smaller;
	}
	
	private static List<String> optionallySplit(String target, List<String> tokens) {
		List<String> smaller = new ArrayList<>();
		for (String token : tokens) 
			// we optionally split based on if the token is bigger than MESSAGE_MAX
			if (token.length() > MESSAGE_MAX) smaller.addAll(split(target, token));
			else smaller.add(token);
		return smaller;
	}
	
	private static List<String> splitOnDots(List<String> tokens) {
		return optionallySplit("\\.", tokens);
	}
	
	private static List<String> splitOnNewlines(List<String> tokens) {
		return optionallySplit("\n", tokens);
	}

	private static List<String> splitOnSpaces(List<String> tokens) {
		return optionallySplit(" ", tokens);
	}
	
	private static List<String> splitOnMarkdowns(String string) {
		Markdown[] markdowns = findMarkdowns(string);
		if (markdowns.length == 0)
			return Arrays.asList(string);
		List<String> strings = new ArrayList<>();
		int start = 0;
		String word, md;
		for (Markdown markdown : markdowns) {
			word = string.substring(start, markdown.start);
			if (markdown.isTooBig)
				md = string.substring(markdown.start+"```markdown".length(), markdown.end-"```".length());
			else 
				md = string.substring(markdown.start, markdown.end);
			if (!word.equals(""))
				strings.add(word);
			strings.add(md);
			start = markdown.end;
		}
		String last = string.substring(start);
		if (!last.equals(""))
			strings.add(last);
		return strings;
	}
	
	private static ConsumedResult mergeBiggest(List<String> words, int index) {
		StringBuilder word = new StringBuilder().append(words.get(index));
		for (String next; index + 1 < words.size(); index++) {
			next = words.get(index + 1);
			if (word.length() + next.length() < MESSAGE_MAX)
				word.append(next);
			else 
				break;
		}
		return new ConsumedResult(word.toString(), ++index);
	}
	
	private static List<String> mergeForDiscord(List<String> splits) { 
		if (splits.size() == 1)
			return splits;
		List<String> merged = new ArrayList<>();
		int i = 0;
		while (i<splits.size()) {
			ConsumedResult result = mergeBiggest(splits, i);
			i = result.end;
			merged.add(result.word);
		}
		return merged;
	}
	
	private static List<String> filterEmpty(List<String> strings) {
		List<String> nonEmpty = new ArrayList<>();
		for (String string : strings)
			if (!string.equals(""))
				nonEmpty.add(string);
		return nonEmpty;
	}

	private static class ConsumedResult {
		public String word;
		public int end;
		
		public ConsumedResult(String word, int consumed) {
			this.word = word;
			this.end = consumed;
		}
	}
	
	public static class Markdown {
		public final int start, end;
		public final boolean isTooBig;
		
		public Markdown(int start, int end) {
			this.start = start;
			this.end = end;
			isTooBig = (end+3)-start >= MESSAGE_MAX;
		}
		
		@Override
		public String toString() {
			return "("+start+","+end+")";
		}
	}
}