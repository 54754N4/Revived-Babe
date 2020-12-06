package lib.scrape;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;

public abstract class Github {
	public static final Browser browser = new Browser(false);
	
	public static enum Dependency {
		LAVAPLAYER(Github::latestLavaplayer), 
		JDA(Github::latestJDA), 
		NAS(Github::latestNAS),
		REFLECTIONS(Github::latestReflections), 
		GSON(Github::latestGson),
		JDBC_SQLITE(Github::latestSqlite);
		
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
	
	public static String latestReflections() {
		return browser.visit("https://github.com/ronmamo/reflections")
				.waitFor(By.cssSelector("article.markdown-body.entry-content.container-lg > .highlight.highlight-text-xml > pre"))
				.getText()
				.replaceAll("[\\s]+", "")
				.replaceAll(".*<version>(.*)</version>.*", "$1");
	}
	
	public static String latestGson() {
		return browser.visit("https://github.com/google/gson")
				.waitFor(By.cssSelector("article.markdown-body.entry-content.container-lg > .highlight.highlight-source-groovy-gradle > pre > span:nth-child(2)"))
				.getText()
				.replace('\'', ' ')
				.trim()
				.split(":")[2];
	}
	
	public static String latestSqlite() {
		return browser.visit("https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc")
				.waitFor(By.cssSelector(".grid.versions > tbody > tr > td:nth-child(2) > a"))
				.getText();
	}
	
	public static void main(String[] args) throws IOException {
		String gradle = Files.readString(Paths.get("build.gradle"));
		List<Dependency> updateable = new ArrayList<>();
		String version;
		try {
			for (Dependency dependency : Github.Dependency.values()) {
				version = dependency.latest.fetch();
				System.out.printf("Checking %s with version %s.. ", dependency.name(), version);
				if (!gradle.contains(version)) {
					updateable.add(dependency);
					System.out.println("Need to update : "+dependency);
				} else
					System.out.println("Up to date.");
			}
		}
		finally { browser.close(); }
	}
}
