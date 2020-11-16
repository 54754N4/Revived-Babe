package lib;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class StringLib {
	public static final int NOT_FOUND = -1;
	public static final String MUSIC_PATH = "D:\\Users\\Satsana\\Desktop\\Media\\Sounds\\Music";

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String works = "~\\Audio\\Songs\\Backstreet Boys\\Millennium\\Show Me the Meaning of Being Lonely.mp3",
				url = "www.google.com",
				url1 = "https://www.google.com/1234qwerqw4";
		System.out.println(consumePrefix("..e hello", new String[] {".."}));
//		System.out.println(deobfuscatePath(works));
//		System.out.println(deobfuscatePath(url));
//		System.out.println(deobfuscatePath(url1));
//		String test = "..alias --name=current --value=\"..schedule --every=2 \\\"..queue --current\\\"\"",
//				test1 = "..schedule --every=2 \\\"..queue --current\\\"";
//		System.out.println(unQuote(test1));
		
//		for (String token : split(unQuote(test1), ' ')) System.out.println(token);
//		String spaced = "..test @[DCN] Xentorno and @Satsana  werqe \\\"@[DCN] Xentorno\\\" qwer  asfd @Satsana";
//		System.out.println(replaceAll(spaced, "@Satsana", "REPLACED", true));
//		System.out.println(replaceAll(spaced, "@[DCN] xentorno", "REPLACED", true));
	}

	public static String consume(String start, String input) {
		if (start.equals(input)) return "";
    	return input.trim().substring(start.length()).trim();
    }
	
	public static boolean startsWithPrefix(String input, String... prefixes) {
		for (String prefix : prefixes)
			if (input.toLowerCase().startsWith(prefix.toLowerCase()))
				return true;
		return false;
	}
	
	public static String consumePrefix(String input, String... prefixes) {
		for (String prefix : prefixes) 
			if (input.toLowerCase().contains(prefix.toLowerCase()))
				return consume(prefix, input);
		return input;
	}
	
	public static String consumeName(String command, String... names) {
		for (String name : names)
			if (command.toLowerCase().startsWith(name.toLowerCase()))
				return command.substring(name.length()).trim();
		return null;
	}
	
	public static String consumeMentions(String command, List<String> mentioned) {
		for (String mention : mentioned) command = command.replaceAll("@"+mention, "");
		return command.trim();
	}
	
	public static String trimMultipleSpaces(String input) {
		return input.replaceAll("\\s+", " ");
	}
	
	public static String consumeMentions(String command) {
		StringBuilder sb = new StringBuilder();
		boolean skip = false;
		for (char c : command.toCharArray()) {
			if (c == '@') skip = true;
			else if (skip && c == ' ') {
				skip = false;
				continue;			// skip last space after mention
			} if (skip) continue;
			sb.append(c);
		}
		return sb.toString();
	}
	
	// ignore separator in quotes
	// skip escaped separators, quotes
	public static String[] split(String input, char separator) {
		if (input.equals("")) return new String[] {input};
		List<String> tokens = new ArrayList<>();
		String token = "";
		char current = '\0', previous;
		boolean inQuotes = false;
		for (int i=0; i<input.length(); i++) {
			previous = current;
			current = input.charAt(i); 
			if (previous != '\\') {
				if (current == '"') inQuotes = !inQuotes;
				else if (current == separator && !inQuotes) {		// consume sep char
					tokens.add(token);
					token = "";
					continue;
				}
			} //else token = token.substring(0, token.length()-1);	//	consume escape char
			token += current;				
		}
		if (!token.equals("")) tokens.add(token);	// add last token 
		return tokens.toArray(new String[tokens.size()]);
	}
	
	public static String replaceAll(String input, String match, String by) {
		return replaceAll(input, match, by, false);
	}
	
	public static String replaceAll(String input, String match, String by, boolean ignoreCase) {
		StringBuilder sb = new StringBuilder();
		int matchSize = match.length(), inputSize = input.length();
		if (ignoreCase) match = match.toLowerCase();
		String forward;
		char current = '\0', previous, ESCAPE = '\\', QUOTES = '"';
		for (int i=0; i<inputSize; i++) {
			forward = "";
			previous = current;
			current = input.charAt(i);
			if (previous != ESCAPE && current == QUOTES && ++i > 0)	{ // last condition is to skip current quote
				sb.append(QUOTES);
				while ((previous = current) != ESCAPE && (current = input.charAt(i++)) != QUOTES)
					sb.append(current);	// skip everything until unescaped quote
				sb.append(QUOTES);
			}
			if (i+matchSize <= inputSize) { 
				forward = input.substring(i, i+matchSize);
				if (ignoreCase) forward = forward.toLowerCase();
			}
			if (forward.equals(match)) {
				i += matchSize-1;	// -1 since loop will also add 1
				sb.append(by);
			} else if (i<inputSize) sb.append(input.charAt(i));
		}
		return sb.toString();
	}
	
	public static int count(String input, char c) {
    	int count = 0; for (char ic : input.toCharArray()) if (ic == c) count++;
    	return count;
    }

	public static String buildPath(String base, String relative) {
		return base + System.getProperty("file.separator") + relative;
	}

	public static String buildMusicPath(String relative) {
		return buildPath(MUSIC_PATH, relative);
	}

	public static String obfuscateMusicFolder(String path) {
		if (isURL(path) || path.startsWith("ytsearch") || path.startsWith("scsearch")) return path;
		return Paths.get(path).normalize().toString().replace(MUSIC_PATH, "~");
	}
	
	public static String deobfuscatePath(String path) {
		if (isURL(path) || path.startsWith("ytsearch") || path.startsWith("scsearch")) return path;
		return Paths.get(path).normalize().toString().replace("~", MUSIC_PATH);
	}

	public static boolean isURL(String url) {
		return url.startsWith("http") || url.startsWith("www.") 
				|| url.matches("(https?:\\/\\/)?(www\\.)?\\w+(\\.\\w+)+\\/?(([^:\\/\\\\\\[\\]@\\s]+\\/?)+)?");
	}

	public static boolean isInteger(String input) {
		if (input.equals("")) return false;
		try { Integer.parseInt(input); } 
		catch (NumberFormatException e) { return false; }
		return true;
	}

	public static boolean isKeyword(String url) {
		return !url.contains("\\") && !url.contains("/");
	}

	public static boolean isPath(String url) {
		return url.matches("([A-Za-z]:\\\\)?((?>[^\\r\\n\\t\\:\\*\\?\"<>\\\\|\\/,\\.]+\\\\?))+(\\.[A-Za-z0-9]+)?");
	}

	public static String join(String... strings) {
		return StringLib.join(" ", strings);
	}

	public static String join(String sep, String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String string : strings) sb.append(string+sep);
		String out = sb.toString();
		if (out.equals("")) return out;
		return out.substring(0,out.length()-1);
	}
	
	public static String join(int start, String... strings) {
		return StringLib.join(" ", start, strings);
	}

	public static String join(String sep, int start, String... strings) {
		return StringLib.join(sep, start, strings.length, strings);
	}

	public static String join(String sep, int start, int end, String... strings) {
		if (end < start) return "ERROR";
		String[] words = new String[end-start];
		for (int i=start; i<end; i++) words[i-start] = strings[i];
		return StringLib.join(words);
	}

	public static String consumeWord(String word, String command) {
		int index = command.indexOf(word);
		if (index != NOT_FOUND)
			return command.substring(0, index) + command.substring(index + word.length());
		return command;
	}

	public static String millisToTime(long millis) {
		return String.format("%02d:%02d:%02d", 
			    TimeUnit.MILLISECONDS.toHours(millis),
			    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
			    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public static long timeToSeconds(String timeStr) {
		int[] conversions = {3600, 60, 1};
		int[] time = StringLib.intify(StringLib.padTime(timeStr).split(":"));
		int duration = 0;
		for (int i=0; i<time.length;  i++) duration += time[i] * conversions[i];
		return duration;
	}

	public static String padTime(String time) {
		if (time.equals("0")) time += "0";
		int colons = StringLib.countColons(time);
		while (colons++ < 2) time = "00:"+time;
		return time;
	}

	public static int countColons(String input) {
		int count = 0;
		for (char c : input.toCharArray()) if (c == ':') count++;
		return count;
	}

	public static int[] intify(String... strs) {
		int[] ints = new int[strs.length];
		int i = 0;
		for (String str : strs) ints[i++] = Integer.parseInt(str);
		return ints;
	}

	//needs to only match 1 needle in the specified haystack
	public static boolean matches(String haystack, String... needles) {	
		boolean matched = false;
		for (String needle : needles) 
			matched |= haystack.toLowerCase().equals(needle.toLowerCase());
		return matched;
	}
	
	public static String simplify(String name) {
		return name.replaceAll("[^A-Za-z0-9\\s]","").toLowerCase();
	}
	
	public static boolean simpleMatch(String input, String match) {
		return simplify(input).contains(simplify(match));
	}
	
	public static boolean isQuoted(String token) {
		if (token == null) return false;
		else if (token.length() == 0) return false;
		return token.charAt(0) == '"' && token.charAt(token.length()-1) == '"';
	}
	
	public static String unQuote(String str) {
		str = str.replace("\\\"", "\"");
		if (str.startsWith("\"") && str.endsWith("\"")) return str.substring(1, str.length()-1);
		return str;
	}
	
	public static String capitalize(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}
	
	public static String multiply(String word, int times) {
		StringBuilder sb = new StringBuilder();
		while (times-->0) sb.append(word);
		return sb.toString();
	}
	
	public static String center(String text, int maxLineSize) {
		if (text.length() >= maxLineSize) return text;
		int count = (maxLineSize - text.length())/2;
		String space = multiply(" ", count-1);
		return space + text + space;
	}
}
