package lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class Balabolka {
	private static final String DIR =  ".\\speak\\balcon\\",
			LIST_VOICES = DIR + "balcon.exe -l", 
			SPEAK_FORMAT = DIR + "balcon.exe -n \"Hazel\" -t \"%s\"",
			WAV_FORMAT = DIR + "balcon.exe -n \"%s\" -t \"%s\" -w \"%s\"";
	public static final Path TTS_WAV_FILE = Paths.get(DIR + "speak.wav");
	
	@SuppressWarnings("unused")
	private static void debug(Process p) throws IOException, InterruptedException {
		p.waitFor();
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line+"\n");
			while ((line = error.readLine()) != null)
				sb.append(line+"\n");
		} 
		System.out.println(sb);
	}
	
	public static void speak(String string) throws InterruptedException, IOException {
		execute(String.format(SPEAK_FORMAT, string)).waitFor();
	}
	
	public static String listVoices() throws InterruptedException, IOException {
		Process p = execute(LIST_VOICES);
		p.waitFor();
		return new ProcOut(p).merge();
	}
	
	public static Path buildWav(String voice, String string) throws InterruptedException, IOException {
		File file = TTS_WAV_FILE.toFile();
		if (file.exists())
			file.delete();
		execute(String.format(WAV_FORMAT, voice, string, TTS_WAV_FILE.toString())).waitFor();
		return TTS_WAV_FILE.normalize();
	}
	
	private static Process execute(String input) throws IOException {
		ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", input);
		return pb.start();
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(Arrays.toString(Arrays.asList("David", "Hazel", "Helena", "Hortense", "Zira").toArray()));
			System.out.println(listVoices());
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}