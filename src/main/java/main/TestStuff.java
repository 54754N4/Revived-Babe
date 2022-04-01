package main;

import com.google.gson.Gson;

import json.LyricsOVHResult;
import lib.HTTP.RequestBuilder;
import lib.HTTP.ResponseHandler;
import lib.encode.Encoder;

public abstract class TestStuff {
	public static void main(String[] args) throws Exception {
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
}


