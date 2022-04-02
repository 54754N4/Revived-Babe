package main;

import java.io.IOException;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.gson.Gson;

import json.LyricsOVHResult;
import lib.HTTP.RequestBuilder;
import lib.HTTP.ResponseHandler;
import lib.encode.Encoder;
import lib.scrape.Browser;
import lib.scrape.Dependency;

public abstract class TestStuff {
	public static void main(String[] args) throws Exception {
//		testLyrics();
		testDependencies();
	}

	public static void testLyrics() throws IOException {
		Gson gson = new Gson();
		String api = String.format(
				"https://api.lyrics.ovh/v1/%s/%s", 
				Encoder.encodeURL("takida"), 
				Encoder.encodeURL("to have and to hold"));
		try (RequestBuilder builder = new RequestBuilder(api)) {
			try (ResponseHandler handler = new ResponseHandler(builder.build());) {
				LyricsOVHResult result = gson.fromJson(handler.getResponse(), LyricsOVHResult.class);
				String lyrics = result.lyrics;
				lyrics = lyrics.replaceAll("\n{3,}", "PLACEHOLDER");
				lyrics = lyrics.replaceAll("\n{2,}", "\n");
				lyrics = lyrics.replace("PLACEHOLDER", "\n\n");
				System.out.println(lyrics);
			}
		}
	}
	
	public static void testDependencies() throws Exception {
		try (Browser b = Browser.getInstance()) {
//			WebElement e = b.waitGet(By.cssSelector(".vbtn.release"));
//			System.out.println(e.getText());
			Dependency.checkUpdates(Dependency::defaultVersionHandler);
		}
	}
}


