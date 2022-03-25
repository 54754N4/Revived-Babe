package json;

public class LyricsOVHResult {
	public String lyrics;

	public LyricsOVHResult(String lyrics) {
		this.lyrics = lyrics;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}
	
	@Override
	public String toString() {
		return lyrics;
	}
}
