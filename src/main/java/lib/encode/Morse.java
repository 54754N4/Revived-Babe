package lib.encode;

import java.util.Map;
import java.util.TreeMap;

public class Morse {
	//https://en.wikipedia.org/wiki/Morse_code
	public static final char[] ALPHABET = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','1','2','3','4','5','6','7','8','9','0', '.', ',', '?', '\'', '!', '/', '(', ')', '&', ':', ';', '=', '+', '-', '_', '"', '$', '@'};
	public static final String[] TRANSLATION = {"._","-...","-.-.","-..",".","..-.","--.","....","..",".---","-.-",".-..","--","-.","---",".--.","--.-",".-.","...","-","..-","...-",".--","-..-","-.--","--..",".----","..---","...--","....-",".....","-....","--...","---..","----.","-----",".-.-.-","--..--","..--..",".----.","-.-.--","-..-.","-.--.","-.--.-",".-...","---...","-.-.-.","-...-",".-.-.","-....-","..--.-",".-..-.","...-..-",".--.-."};
	
	public static final Map<Character, String> TABLE = new TreeMap<>();
	public static final Map<String, Character> REVERSE_TABLE = new TreeMap<>();
	
	static { 
		for (int i=0; i<ALPHABET.length; i++) {
			TABLE.put(ALPHABET[i], TRANSLATION[i]);
			REVERSE_TABLE.put(TRANSLATION[i], ALPHABET[i]);
		}
	}
	
	public static String encode(String english) {
		StringBuilder sb = new StringBuilder();
		for (char c : english.toLowerCase().toCharArray()) 
			sb.append((c == ' ') ? "  " : TABLE.get(c) + ' ');
		return sb.toString().trim();
	}
	
	public static String decode(String morse) {
		StringBuilder sb = new StringBuilder();
		String[] words = morse.split("   ");
		for (String word : words) {
			for (String character : word.split(" ")) 
				sb.append(REVERSE_TABLE.get(character));
			sb.append(" ");
		}
		return sb.toString().trim();
	}
	
	public static void main(String[] args) {
		String text = "The greatest glory in living lies not in never falling, but in rising every time we fall",
				morse = encode(text),
				english = decode(morse);
		System.out.println(text);
		System.out.println(morse);
		System.out.println(english);
	}
}
