package lib.scrape;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

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
	
	public final Callable<String> latest;
	
	private Dependency(Callable<String> latest) {
		this.latest = latest;
	}
	
	public static String latestLavaplayer() {
		return Browser.getInstance()
			.visit("https://github.com/sedmelluq/lavaplayer/tags")
			.waitGet(By.cssSelector(".Box-row:nth-child(2) > .flex-auto > .commit > .d-flex > .flex-auto > a"))
			.getText()
			.trim();
	}
	
	public static String latestJDA() {
		return Browser.getInstance()
			.visit("https://ci.dv8tion.net/job/JDA/lastSuccessfulBuild")
			.waitGet(By.cssSelector(".fileList > tbody > tr:nth-child(6) > td:nth-child(2) > a"))
			.getText()
			.trim()
			.replace("JDA-", "")
			.replace(".jar", "");
	}
	
	public static String latestNAS() {
		return latestMaven("https://mvnrepository.com/artifact/com.sedmelluq/jda-nas?repo=jcenter");
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

	// retrieves newest release version from Maven url
	public static String latestMaven(String url) {	
		return Browser.getInstance()
			.visit(url)
			.waitGet(By.cssSelector(".vbtn.release"))
			.getText();
	}

	public static String latestMaven(String groupID, String artifactID) {
		return latestMaven(groupID, artifactID, "jcenter");
	}
	
	public static String latestMaven(String groupID, String artifactID, String repo) {
		return Browser.getInstance()
				.visit(String.format("https://mvnrepository.com/artifact/%s/%s?repo=%s", groupID, artifactID, repo))
				.waitGet(By.cssSelector(".vbtn.release"))
				.getText();
	}
	
	public static void checkUpdates(VersionCheckHandler callback) throws Exception {
		if (callback == null)
			return;
		String gradle = Files.readString(Paths.get("build.gradle")), version;
		for (Dependency dependency : Dependency.values()) 
			callback.handle(
					dependency.name(), 
					version = dependency.latest.call(), 
					gradle.contains(version));
	}
	
	public static void defaultVersionHandler(String name, String latest, boolean isUpdated) {
		logger.info("Fetched {} for {}: {}.", latest, name, isUpdated ? "updated" : "NEEDS UPDATE");
	}
	
	@FunctionalInterface
	public static interface VersionCheckHandler {
		void handle(String name, String latest, boolean isUpdated);
	}
	
	public static void main(String[] args) throws Exception {
		try { 
			checkUpdates(Dependency::defaultVersionHandler);
//			System.out.println(latestMaven("org.dyn4j", "dyn4j"));
		} finally { Browser.getInstance().close(); }
	}
}
