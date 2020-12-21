package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class StreamGobbler implements Callable<String> {
	private InputStream stream;
	
	public StreamGobbler(InputStream stream) {
		this.stream = stream;
	}
	
	@Override
	public String call() throws IOException {
		final StringBuilder out = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			reader.lines()
				.forEach(line -> out.append(line + System.lineSeparator()));
		}
		return out.toString();
	}
}