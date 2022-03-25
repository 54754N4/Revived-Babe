package lib;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.LinkedList;

public class LineShiftBuffer {
	private int bufferSize;
	private BufferedReader reader;
	private LinkedList<String> buffer;
	
	private LineShiftBuffer(int bufferSize, BufferedReader in) {
		buffer = new LinkedList<>();
		reader = in;
		this.bufferSize = bufferSize;
	}
	
	public String pop() throws IOException {
		String line = reader.readLine();
		return line == null ? null : shift(line);
	}

	private String shift(String line) {
		if (buffer.size() == bufferSize)
			buffer.removeFirst();
		buffer.add(line);
		return line;
	}
	
	public static LinkedList<String> getTail(int size, InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		LineShiftBuffer shifter = new LineShiftBuffer(size, reader);
		String line = shifter.pop();
		while (line != null)
			line = shifter.pop();
		reader.close();
		return shifter.buffer;
	}
	
	public static void main(String[] args) throws IOException {
		getTail(5, new FileInputStream(Paths.get("logs\\current-babe.log").toFile()))
			.forEach(System.out::println);
	}
}