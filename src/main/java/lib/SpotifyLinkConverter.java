package lib;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lib.scrape.Browser;

public class SpotifyLinkConverter {
	private final static Logger logger = LoggerFactory.getLogger(SpotifyLinkConverter.class);
	private static SpotifyLinkConverter INSTANCE;
	
	public static final synchronized SpotifyLinkConverter getInstance() {
		if (INSTANCE == null)
			INSTANCE = new SpotifyLinkConverter();
		return INSTANCE;
	}
	
	public List<String> convertPlaylist(String url) {
		Browser browser = Browser.getInstance()
				.visit(url)
				.waitUntilLoaded();
		List<WebElement> rows = browser.findElements(By.cssSelector("[role=row]")),
				cols;
		List<String> songs = new ArrayList<>();
		String song;
		for (int i=1; i<rows.size(); i++) {
			cols = rows.get(i).findElements(By.cssSelector("[role=gridcell][aria-colindex]:nth-child(2) > div:nth-child(2) > *"));
			if (cols.size() == 3)
				song = cols.get(0).getText() + " " + cols.get(2).getText();
			else	// since second would be the "E" for explicit symbol
				song = cols.get(0).getText() + " " + cols.get(1).getText();
			songs.add(song);
			logger.info("Extracted : "+song);
		}
		logger.info(String.format("Extracted %d songs from url %s", songs.size(), url));
		return songs;
	}
	
	public static void main(String[] args) {
		try {
			getInstance()
				.convertPlaylist("https://open.spotify.com/playlist/6LrZjBf5dvA24ClcU1H3IM")
				.forEach(System.out::println);
		} finally {
			Browser.getInstance().close();
		}
	}
}
