package lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commands.hierarchy.DiscordCommand;

/**
 * Performance boost strategy takes 2 steps and is as follows : 
 * 1. if msg is less than MESSAGE_MAX -> it won't be touched 
 * 		else -> subdivide the message based on code blocks into tokens
 * 		if no code blocks or is still too big -> split token on newlines
 * 		if still too big -> split token on dots
 * at this point, we suppose that after these 3 kinds of splits (worst case), 
 * tokens should all become valid but small discord messages.
 * 2. merge tokens as long as satisfy length < MESSAGE_MAX
 * 
 * This allows DiscordCommands to chain MessageActions (e.g. network I/O) 
 * using the tokens we give it since they're all the biggest/valid we could
 * make from the command output. This greatly reduces the number of network 
 * calls we initiate to Discord, and directly translates to the bot replying 
 * faster no matter the output length.
 */
public abstract class PrintBooster {

//	@SuppressWarnings("unused")
//	public static void main(String[] args) {
//		String str = "```markdown\n#[unqueue, remove, rem, rm]\nUsage: <name> <indices>\n\tDeletes the songs specified as parameter, each index separated by spaces.\nUsage: <name> [-t|--tag] <pattern>\n\tDeletes the tags that matched the given pattern.\n\n```";
//		String lyrics = "```markdown\n\nFirst things first \nI'mma say all the words inside my head \nI'm fired up and tired of the way that things have been, oh-ooh \nThe way that things have been, oh-ooh \nSecond thing second \nDon't you tell me what you think that \nI can be \nI'm the one at the sail, \nI'm the master of my sea, oh-ooh \nThe master of my sea, oh-ooh\n\nI was broken from a young age \nTaking my soul into the masses \nWrite down my poems for the few \nThat looked at me \nTook to me, shook to me, feeling me \nSinging from heart ache from the pain \nTake up my message from the veins \nSpeaking my lesson from the brain \nSeeing the beauty through the...\n\nPain! \nYou made me a, you made me a believer, believer \nPain! \nYou break me down, you build me up, believer, believer \nPain! \nI let the bullets fly, oh let them rain \nMy luck, my love, my \nGod, they came from... \nPain! \nYou made me a, you made me a believer, believer\n\nThird things third \nSend a prayer to the ones up above \nAll the hate that you've heard has turned your spirit to a dove, oh-ooh \nYour spirit up above, oh-ooh\n\nI was choking in the crowd \nLiving my brain up in the cloud \nFalling like ashes to the ground \nHoping my feelings, they would drown \nBut they never did, ever lived, ebbing and flowing \nInhibited, limited \nTill it broke up and it rained down \nIt rained down, like...\n\nPain! \nYou made me a, you made me a believer, believer \nPain! \nYou break me down, you built me up, believer, believer \nPain! \nI let the bullets fly, oh let them rain \nMy luck, my love, my \nGod, they came from... \nPain! \nYou made me a, you made me a believer, believer\n\nLast things last \nBy the grace of the fire and the flames \nYou're the face of the future, the blood in my veins, oh-ooh \nThe blood in my veins, oh-ooh \nBut they never did, ever lived, ebbing and flowing \nInhibited, limited \nTill it broke up and it rained down \nIt rained down, like...\n\nPain! \nYou made me a, you made me a believer, believer \nPain!\nYou break me down, you built me up, believer, believer \nPain! \nI let the bullets fly, oh let them rain \nMy luck, my love, my \nGod, they came from... \nPain! \nYou made me a, you made me a believer, believer\n\n```";
//		List<String> words = splitForDiscord(lyrics);
//		System.out.println(words.size());
//		for (String word : words)
//			System.out.println(word);
//	}

	public static Markdown[] findMarkdowns(String sb) {
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
	
	private static List<String> splitOnTarget(String target, String word) {
		List<String> smaller = new ArrayList<>();
		smaller.addAll(Arrays.asList(word.split(target)));
		int i = 0;
		for (String s : smaller) 
			smaller.set(i++, s+target);	// since we dont want to consume the target
		return smaller;
	}
	
	private static List<String> optionallySplitOn(String target, List<String> tokens) {
		List<String> smaller = new ArrayList<>();
		for (String token : tokens) 
			// we optionally split based on if the token is bigger than MESSAGE_MAX
			if (token.length() > DiscordCommand.MESSAGE_MAX) smaller.addAll(splitOnTarget(target, token));
			else smaller.add(token);
		return smaller;
	}
	
	private static List<String> splitOnDots(List<String> tokens) {
		return optionallySplitOn("\\.", tokens);
	}
	
	private static List<String> splitOnNewlines(List<String> tokens) {
		return optionallySplitOn("\n", tokens);
	}

	private static List<String> splitOnMarkdowns(String string) {
		Markdown[] markdowns = findMarkdowns(string);
		if (markdowns.length == 0) {
			List<String> single = new ArrayList<>(1);
			single.add(string);
			return single;
		}
		List<String> strings = new ArrayList<>();
		int start = 0;
		String word, md;
		for (Markdown markdown : markdowns) {
			word = string.substring(start, markdown.start);
			if (markdown.isTooBig)
				md = string.substring(markdown.start+"```markdown".length(), markdown.end-"```".length());
			else 
				md = string.substring(markdown.start, markdown.end);
			start = markdown.end;			// next start is previous markdown end
			if (!word.equals("")) strings.add(word); 
			strings.add(md);
		}
		String last = string.substring(start);
		if (!last.equals(""))
			strings.add(last);
		return strings;
	}
	
	private static ConsumedResult mergeBiggest(List<String> words, int index) {
		StringBuilder word = new StringBuilder().append(words.get(index));
		String next;
		while (index + 1 < words.size() && word.length() + (next = words.get(index + 1)).length() < DiscordCommand.MESSAGE_MAX) {
			word.append(next);
			index++;
		}
		if (index + 1 != words.size()-1)	// not last word, that means word is too big
			return new ConsumedResult(word.toString(), ++index);
		String last = words.get(index);
		if (word.length() + last.length() < DiscordCommand.MESSAGE_MAX) 
			word.append(last);
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
	
	public static List<String> splitForDiscord(String string) {
		return filterEmpty(mergeForDiscord(splitOnDots(splitOnNewlines(splitOnMarkdowns(string)))));
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
			isTooBig = (end+3)-start >= DiscordCommand.MESSAGE_MAX;
		}
		
		@Override
		public String toString() {
			return "("+start+","+end+")";
		}
	}
}
