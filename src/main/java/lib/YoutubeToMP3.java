package lib;

import java.io.IOException;

import commands.hierarchy.RestCommand;
import json.YTMP3AudioInfo;
import json.YTMP3AudioInfo.Formats.Audio;
import json.YTMP3StatusInfo;
import lib.encode.Encoder;
import okhttp3.Request;
import okhttp3.Response;

/* Sends custom requests of onlymp3.to to get mp3 download link. 
 * Imitates the website in how the requests are processed.
 **/
public class YoutubeToMP3 implements Runnable {
	private static final long SLEEP_DELAY = 3000;	// same as website does it
	private final String url;
	private final StatusListener listener;
	
	public YoutubeToMP3(String url, StatusListener listener) {
		this.url = url;
		this.listener = listener;
	}
	
	private final Response request(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("Accept-Language", "en-GB,en;q=0.5")
				.header("Accept-Encoding", "gzip, deflate, br")
			    .header("Origin", "https://onlymp3.to")
			    .header("Referer", "https://onlymp3.to/")
			    .header("DNT", "1")
			    .header("Sec-Fetch-Dest", "empty")
			    .header("Sec-Fetch-Mode", "cors")
			    .header("Sec-Fetch-Site", "same-site")
			    .header("Sec-GPC", "1")
				.build();
		return RestCommand.execute(request);
	}
	
	@Override
	public void run() {
		if (!url.contains("youtube")) {
			listener.onError(new IllegalArgumentException("Can only download from youtube URLs"));
			return;
		}
		try {
			// Request track info
			String url = Encoder.encodeURL(this.url);
			Response response = request(String.format("http://srv8.onlymp3.to/listFormats?url=%s", url));
			YTMP3AudioInfo audioInfo = RestCommand.convert(YTMP3AudioInfo.class, response);
			listener.onRequest(audioInfo);
			Audio[] audios = audioInfo.formats.audio;
			url = audios[audios.length-1].url;
			// Request conversion
			response = request(url);
			YTMP3StatusInfo statusInfo = RestCommand.convert(YTMP3StatusInfo.class, response);
			listener.onConvert(statusInfo);
			url = statusInfo.url;
			// Request url when ready
			do {
				response = request(url);
				statusInfo = RestCommand.convert(YTMP3StatusInfo.class, response);
				listener.onCheck(statusInfo);
				if (statusInfo.error == true)
					throw new IllegalStateException(statusInfo.status);
				if (statusInfo.status.equals("ready"))
					break;
				Thread.sleep(SLEEP_DELAY);
			} while (statusInfo.url.equals(""));
			listener.onFinished(statusInfo.url);
		} catch (Exception e) {
			listener.onError(e);
		}
	}
	
	public static interface StatusListener {
		void onRequest(YTMP3AudioInfo info);
		void onConvert(YTMP3StatusInfo info);
		void onCheck(YTMP3StatusInfo info);
		void onFinished(String url);
		void onError(Throwable t);
	}
}
