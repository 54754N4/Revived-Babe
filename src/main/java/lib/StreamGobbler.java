package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
	private InputStream stream;
	private Appender callback;
	private int target;
	
	public StreamGobbler(InputStream stream, Appender callback, int target) {
		this.stream = stream;
		this.callback = callback;
		this.target = target;
	}
	
	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			reader.lines().forEach((line) -> { callback.append(target, line+"\n"); } );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}