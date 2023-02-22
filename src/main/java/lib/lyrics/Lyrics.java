package lib.lyrics;

public class Lyrics {
	private final String title, author, content, url, source;

	protected Lyrics(String title, String author, String content, String url, String source) {
		this.title = title;
		this.author = author;
		this.content = content;
		this.url = url;
		this.source = source;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getURL() {
		return url;
	}

	public String getSource() {
		return source;
	}
}