package lib.scrape;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
//import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.safari.SafariOptions;

public interface Options {
	
	/* Constants */
	
	static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();

	/* Driver arguments */
	
	static final String
		ARG_HEADLESS = "--headless",
		ARG_DISABLE_GPU = "--disable-gpu",
		ARG_DISABLE_EXTENSIONS = "--disable-extensions",
		ARG_WINDOW_SIZE = String.format("--window-size=%s,%s", SCREEN.width, SCREEN.height)	;
	
	/* Specific browser configs */
	
	static final Firefox FIREFOX = new Firefox();
	static final Chrome CHROME = new Chrome();
	static final Edge EDGE = new Edge();
	static final Safari SAFARI = new Safari();
	static final InternetExplorer INTERNET_EXPLORER = new InternetExplorer();
	
	static Firefox firefox() {
		return FIREFOX;
	}
	
	static Chrome chrome() {
		return CHROME;
	}
	
	static Edge edge() {
		return EDGE;
	}
	
	static Safari safari() {
		return SAFARI;
	}
	
	static InternetExplorer internetExplorer() {
		return INTERNET_EXPLORER;
	}
	
	/* All configuration methods that need to be created for each browser */
	
	static interface ConfigurationMethods<T> {
		T defaultSettings();
		default T debugging(T options) {
			throw new IllegalStateException("Browser doesn't support headless and other debugging features.");
		}
	}
	
	public static class Firefox implements ConfigurationMethods<FirefoxOptions> {
		@Override
		public FirefoxOptions defaultSettings() {
			return new FirefoxOptions()
                    .addArguments(
                    		ARG_HEADLESS,
                    		ARG_DISABLE_GPU,
                    		ARG_WINDOW_SIZE
                    );
		}

		@Override
		public FirefoxOptions debugging(FirefoxOptions options) {
			return options;
		}
	}

	public static class Chrome implements ConfigurationMethods<ChromeOptions> {
		@Override
		public ChromeOptions defaultSettings() {
			return (ChromeOptions) new ChromeOptions()
					.setAcceptInsecureCerts(true)
	                .addArguments(ARG_HEADLESS, ARG_DISABLE_GPU, ARG_WINDOW_SIZE);
		}
	
		@Override
		public ChromeOptions debugging(ChromeOptions options) {
			return options;
		}
	}
	
	public static class Edge implements ConfigurationMethods<EdgeOptions> {	
		@Override
		public EdgeOptions defaultSettings() {
			return (EdgeOptions) new EdgeOptions()
					.setAcceptInsecureCerts(true)
	                .addArguments(ARG_HEADLESS, ARG_DISABLE_GPU, ARG_WINDOW_SIZE);
		}
	
		@Override
		public EdgeOptions debugging(EdgeOptions options) {
			return options;
		}
	}
	
	public static class Safari implements ConfigurationMethods<SafariOptions> {
		@Override
		public SafariOptions defaultSettings() {
			return new SafariOptions()
					.setAcceptInsecureCerts(true);
		}
	}
	
	public static class InternetExplorer implements ConfigurationMethods<InternetExplorerOptions> {
		@Override
		public InternetExplorerOptions defaultSettings() {
			return new InternetExplorerOptions()
					.setAcceptInsecureCerts(true);
		}
	}
}
