package tests.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import com.google.gson.Gson;

import annotations.SlowTest;
import commands.level.normal.Lyrics;
import json.LyricsOVHResult;
import lib.HTTP.RequestBuilder;
import lib.HTTP.ResponseHandler;
import lib.encode.Encoder;

public class TestLyrics {
	
	@SlowTest
	public void givenOVHRequest_LyricsResponse_shouldBeValid() throws MalformedURLException, IOException {
		Gson gson = new Gson();
		String api = String.format(
				"https://api.lyrics.ovh/v1/%s/%s", 
				Encoder.encodeURL("takida"), 
				Encoder.encodeURL("to have and to hold"));
		try (RequestBuilder builder = new RequestBuilder(api)) {
			try (ResponseHandler handler = new ResponseHandler(builder.build());) {
				LyricsOVHResult result = gson.fromJson(handler.getResponse(), LyricsOVHResult.class);
				String lyrics = result.lyrics;
				lyrics = Lyrics.fixSpacing(lyrics);
				assertEquals(EXPECTED, lyrics);
			}
		}
	}
	
	public static String EXPECTED = "You're forgiveness is all mine\r\n"
			+ "All you said was lies\r\n"
			+ "You hard to see in eyes\r\n"
			+ "Yeah, yeah, yeah\r\n"
			+ "I'm a lot of love\n"
			+ "Yeah, yeah, yeah\n"
			+ "For you it's on blood\n"
			+ "\n"
			+ "You kill my only trust and it hurts\n"
			+ "Say goodbye I cannot believe that I turn straigh\n"
			+ "My hate for you had grown strong\n"
			+ "You see, I know what you said\n"
			+ "No more to have and to hold\n"
			+ "\n"
			+ "All our heart that rich soul\n"
			+ "You deserve nothing left\n"
			+ "Not more to have and to hold\n"
			+ "\n"
			+ "We can give our run line\n"
			+ "I know it's on your mind\n"
			+ "Down for any time\n"
			+ "\n"
			+ "Yeah, yeah, yeah\n"
			+ "I'm a lot of love\n"
			+ "Yeah, yeah, yeah\n"
			+ "For you it's on blood\n"
			+ "\n"
			+ "You kill my only trust and it hurts\n"
			+ "Say goodbye I cannot believe that I turn straight\n"
			+ "My hate for you had grown strong\n"
			+ "You see, I know what you said\n"
			+ "No more to have and to hold\n"
			+ "\n"
			+ "No one answers the phone\n"
			+ "No end in night\n"
			+ "And I'm here standing alone\n"
			+ "And I still wandering way\n"
			+ "See that it's no one home\n"
			+ "You're bleeding\n"
			+ "\n"
			+ "With you I only 'standing\n"
			+ "It hurts, it hurts, it hurts\n"
			+ "\n"
			+ "I cannot believe that I turn straight\n"
			+ "My hate for you had grown strong\n"
			+ "You see, I know what you said\n"
			+ "No more to have and to hold\n"
			+ "\n"
			+ "No more to have\n"
			+ "No more to have and to hold";
}
