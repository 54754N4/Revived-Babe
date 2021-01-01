package spelling;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.gson.JsonIOException;

import commands.name.Command;
import lib.debug.Duration;

public class SpellingCorrector {
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	private final Map<String,Integer> dict;
	
	public SpellingCorrector() {
		this(Arrays.asList(Command.values())
				.stream()
				.map(Command::getNames)
				.flatMap(Stream::of)
				.collect(Collectors.toList()));
	}
	
	public SpellingCorrector(Collection<String> language) {
		dict = new ConcurrentHashMap<>();
		BiFunction<String, Integer, Integer> incrementor = (k, v) -> v == null ? 1 : v + 1;
		language.forEach(word -> dict.compute(word, incrementor));
	}
	
	public Comparator<? super String> comparator() {
		return (a,b) -> dict.get(a) - dict.get(b);	// sorts based on occurrences count in initial language
	}
	
	public String correct(String word) {
		return correct(word, 2);
	}
	
	public String correct(String word, int corrections) {
		if (dict.containsKey(word))
			return word;
		Stream<String> editsn;
		for (int distance=1; distance<=corrections; distance++) {
			editsn = edits1(word);
			for (int i=0; i<distance; i++)
				editsn = editsn.map(SpellingCorrector::edits1)
					.flatMap(x -> x);
			Optional<String> correction = editsn.filter(dict::containsKey)
					.max(comparator()); 
			if (correction.isPresent())
				return correction.get();
			editsn.close();
		}
		return word;
	}
	
	private static Stream<String> edits1(final String word){
		Stream<String> deletes = deletes(word),
			replaces = replaces(word),
			inserts = inserts(word),
			transposes = transposes(word);
		return Stream.of(deletes, replaces, inserts, transposes)
				.flatMap(x -> x);
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
	
	public static void main(String[] args) throws JsonIOException, IOException {
		SpellingCorrector corrector = new SpellingCorrector();
		System.out.println(Duration.of(corrector::correct, "echiwa", 4));
	}
}


/*

	public static Stream<String> edits1(final String word) {
		List<Function<String, Stream<String>>> operations = Arrays.asList(
			SpellingCorrector::deletes,
			SpellingCorrector::replaces,
			SpellingCorrector::inserts,
			SpellingCorrector::transposes
		);
		List<GeneratorTask> tasks = operations.stream()
			.map(op -> new GeneratorTask(word, op))
			.collect(Collectors.toList());
		return ForkJoinTask.invokeAll(tasks)
			.stream()
			.map(ForkJoinTask::join)
			.flatMap(Stream::of)
			.flatMap(x -> x);		
	}
	
	public String correct(String word) {
		return correct(word, 2);
	} 
	
	public String correct(String word, int corrections) {
		if (dict.containsKey(word))
			return word;
		return process(word, corrections);
	}
	

 	// Processes in parallel corrections based on
 	// different edit distances.
	private String process(String word, int corrections) {
		List<CorrectionTask> tasks = new ArrayList<>();
		for (int distance=0; distance <= corrections; distance++)
			tasks.add(new CorrectionTask(word, distance));
		Optional<Optional<String>> wrappedResult = ForkJoinTask.invokeAll(tasks)
				.stream()
				.map(ForkJoinTask::join)	 	// execute tasks
				.filter(Optional::isPresent)	// keep successful ones
				.findFirst();					// wraps Optional<String> with Optional
		return wrappedResult.isEmpty() || wrappedResult.get().isEmpty() ?
			word : 
			wrappedResult.get().get();
	}

	private final class CorrectionTask extends RecursiveTask<Optional<String>> {
		private static final long serialVersionUID = 3566465502726714361L;
		
		private final String word;
		private final int distance;
		
		public CorrectionTask(String word, int distance) {
			this.word = word;
			this.distance = distance;
		}
		
		@Override
		protected Optional<String> compute() {
			Stream<String> editsn = edits1(word);
			for (int i=1; i<distance; i++)
				editsn = editsn.map(SpellingCorrector::edits1)
					.flatMap(x -> x);
			return known(editsn)
				.max(comparator());
		}
	}
	
	private static final class GeneratorTask extends RecursiveTask<Stream<String>> {
		private static final long serialVersionUID = -5732728077068319684L;
		
		private final String word;
		private final Function<String, Stream<String>> mapper;
		
		public GeneratorTask(String word, Function<String, Stream<String>> mapper) {
			this.word = word;
			this.mapper = mapper;
		}
		
		@Override
		protected Stream<String> compute() {
			return mapper.apply(word);
		}
	}
*/