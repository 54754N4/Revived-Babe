package lib.scrape;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Dependency {
	LAVAPLAYER(Dependency::latestLavaplayer), 
	JDA(Dependency::latestJDA), 
	NAS(Dependency::latestNAS),
	LOGBACK(Dependency::latestLogback),
	REFLECTIONS(Dependency::latestReflections), 
	GSON(Dependency::latestGson),
	SELENIUM(Dependency::latestSelenium),
	WEB_DRIVER(Dependency::latestWebDriver),
	JDBC_SQLITE(Dependency::latestSqlite),
	JGRAPHT(Dependency::latestJGraphT),
	JS_ENGINE(Dependency::latestJSEngine);
	
	private static final Logger logger = LoggerFactory.getLogger(Dependency.class);
	public static final Browser browser = new Browser(true);
	
	public final Callable<String> latest;
	
	private Dependency(Callable<String> latest) {
		this.latest = latest;
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
	public static void checkUpdates(@Nullable LatestVersionCallback callback) throws Exception {
		String gradle = Files.readString(Paths.get("build.gradle")),
			version, message;
		boolean isUpdated;
		for (Dependency dependency : Dependency.values()) {
			version = dependency.latest.call();
			message = String.format("Checking %s for version %s... ", dependency.name(), version);
			isUpdated = gradle.contains(version);
			message += isUpdated ? "Up to date." : "NEEDS UPDATE.";
			if (callback != null) callback.handle(dependency.name(), version, isUpdated);
			logger.info(message);
		}
	}
	
	public static void checkUpdates() throws Exception {
		checkUpdates(null);
	}
	
	@FunctionalInterface
	public static interface LatestVersionCallback {
		void handle(String name, String latest, boolean isUpdated);
	}

	public static void main(String[] args) throws Exception {
		try { checkUpdates(); }
		finally { browser.close(); }
	}
}
