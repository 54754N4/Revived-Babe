package spelling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SpellingCorrector {
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	private final BiFunction<String, Integer, Integer> counter;
	private final Map<String,Integer> dict;

	public SpellingCorrector(Path dictionaryFile) throws IOException {
		dict = new HashMap<>();
		counter = (k, v) -> v == null ? 1 : v + 1;
		Stream.of(
				new String(Files.readAllBytes(dictionaryFile))
					.toLowerCase()
					.replaceAll("[^a-z ]","")
					.split(" ")
		).forEach(word -> dict.compute(word, counter));
	}

	private Stream<String> edits1(final String word){
		Stream<String> deletes = deletes(word),
			replaces = replaces(word),
			inserts = inserts(word),
			transposes = transposes(word);
		return Stream.of(deletes, replaces, inserts, transposes)
				.flatMap(x -> x);
	}

	private Stream<String> known(Stream<String> words){
		return words.filter(dict::containsKey);
	}
	
	public String correct(String word){
		Optional<String> edits1 = known(edits1(word))
				.max((a,b) -> dict.get(a) - dict.get(b));
		Optional<String> edits2 = known(
				edits1(word)
					.map(w2 -> edits1(w2))
					.flatMap(x -> x))
				.max((a,b) -> dict.get(a) - dict.get(b));
		return dict.containsKey(word) ? 
				word : 
				(edits1.isPresent() ? 
						edits1.get() : 
						(edits2.isPresent() ? edits2.get() : word));
	}
	
	public static Stream<String> deletes(final String word) {
		return IntStream.range(0, word.length())
				.mapToObj(i -> word.substring(0, i) + word.substring(i + 1));
	}

	public static Stream<String> replaces(final String word) {
		return IntStream.range(0, word.length())
				.mapToObj(i -> i)
				.flatMap(i -> alphabet.chars()
						.mapToObj(c -> word.substring(0,i) + (char)c + word.substring(i+1)));
	}

	public static Stream<String> inserts(final String word) {
		return IntStream.range(0, word.length() + 1)
				.mapToObj(i -> i)
				.flatMap(i -> alphabet.chars()
						.mapToObj(c -> word.substring(0,i) + (char)c + word.substring(i)));
	}
	
	public static Stream<String> transposes(final String word) {
		return IntStream.range(0, word.length() - 1)
				.mapToObj(i-> word.substring(0, i) + word.substring(i+1, i+2) + word.charAt(i) + word.substring(i+2));
	}
}