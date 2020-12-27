package lib.scrape;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Github {
	private static final Logger logger = LoggerFactory.getLogger(Github.class);
	public static final Browser browser = new Browser(true);
	
	public static enum Dependency {
		LAVAPLAYER(Github::latestLavaplayer), 
		JDA(Github::latestJDA), 
		NAS(Github::latestNAS),
		LOGBACK(Github::latestLogback),
		REFLECTIONS(Github::latestReflections), 
		GSON(Github::latestGson),
		SELENIUM(Github::latestSelenium),
		WEB_DRIVER(Github::latestWebDriver),
		JDBC_SQLITE(Github::latestSqlite),
		JGRAPHT(Github::latestJGraphT),
		JS_ENGINE(Github::latestJSEngine);
		
		public final VersionFetcher latest;
		
		private Dependency(VersionFetcher latest) {
			this.latest = latest;
		}
		
		@FunctionalInterface
		public static interface VersionFetcher {
			String fetch();
		}
	}
	
	public static String latestLavaplayer() {
		return browser.visit("https://github.com/sedmelluq/lavaplayer/tags")
			.waitFor(By.cssSelector(".Box-row:nth-child(2) > .flex-auto > .commit > .d-flex > .flex-auto > a"))
			.getText()
			.trim();
	}
	
	public static String latestJDA() {
		return browser.visit("https://ci.dv8tion.net/job/JDA/lastSuccessfulBuild")
			.waitFor(By.cssSelector(".fileList > tbody > tr:nth-child(6) > td:nth-child(2) > a"))
			.getText()
			.trim()
			.replace("JDA-", "")
			.replace(".jar", "");
	}
	
	public static String latestNAS() {
		return browser.visit("https://api.bintray.com/packages/sedmelluq/com.sedmelluq/jda-nas")
			.waitFor(By.cssSelector("tbody > tr.treeRow.stringRow.opened:nth-child(23) > .treeValueCell.stringCell > span > .objectBox.objectBox-string"))
			.getText()
			.replace('"', ' ')
			.trim();
	}
	
	public static String latestLogback() {
		return latestMaven("https://mvnrepository.com/artifact/ch.qos.logback/logback-classic");
	}
	
	public static String latestReflections() {
		return latestMaven("https://mvnrepository.com/artifact/org.reflections/reflections");
	}
	
	public static String latestGson() {
		return latestMaven("https://mvnrepository.com/artifact/com.google.code.gson/gson");
	}
	
	public static String latestSelenium() {
		return latestMaven("https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java");
	}
	
	public static String latestWebDriver() {
		return latestMaven("https://mvnrepository.com/artifact/io.github.bonigarcia/webdrivermanager");
	}
	
	public static String latestSqlite() {
		return latestMaven("https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc");
	}
	
	public static String latestJGraphT() {
		return latestMaven("https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core");
	}

	public static String latestJSEngine() {
		return latestMaven("https://mvnrepository.com/artifact/org.graalvm.js/js-scriptengine");
	}
	
	// Helper method
	
	public static String latestMaven(String url) {	// retrieves newest release version
		return browser.visit(url)
				.waitFor(By.cssSelector(".vbtn.release"))
				.getText();
	}
	
	// Returns a dictionary that maps each dependency to an isUpdated boolean 
	public static Map<String, Boolean> checkUpdates() throws IOException {
		Map<String, Boolean> updates = new HashMap<>();
		String gradle = Files.readString(Paths.get("build.gradle")),
			version, message;
		boolean isUpdated;
		for (Dependency dependency : Github.Dependency.values()) {
			version = dependency.latest.fetch();
			message = String.format("Checking %s for version %s... ", dependency.name(), version);
			isUpdated = gradle.contains(version);
			message += isUpdated ? "Up to date." : "NEEDS UPDATE.";
			updates.put(dependency.name(), isUpdated);
			logger.info(message);
		}
		return updates;
	}
	
	public static void main(String[] args) throws IOException {
		try {
			Map<String, Boolean> updates = checkUpdates();
			System.out.println(updates);
		}
		finally { browser.close(); }
	}
}
