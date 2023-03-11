package lib.scrape;

import java.util.function.Function;
import java.util.function.Supplier;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;

public class BrowserConfigurator<K> {
	private static final Logger logger = LoggerFactory.getLogger(BrowserConfigurator.class);
	private final Supplier<K> options;
	private final Function<K, RemoteWebDriver> creator;
	private final DriverManagerType type;
	
	private BrowserConfigurator(Supplier<K> options, Function<K, RemoteWebDriver> creator, DriverManagerType type) {
		this.options = options;
		this.creator = creator;
		this.type = type;
	}
	
	/* Checks that a specific browser driver is already 
	 * downloaded, otherwise downloads correct version.
	 */
	public RemoteWebDriver createDriver() {
		logger.info("Setting up webdriver type: {}", type);
		WebDriverManager manager = WebDriverManager.getInstance(type)
				.disableCsp();
		logger.info("Setting targeted for windows: {}", Constants.isWindows);
		manager = Constants.isWindows ? manager.win() : manager.linux();
		logger.info("CPU is ARM: {}", Constants.isArm);
		if (Constants.isArm)
			manager = manager.arm64();
		else
			manager = Constants.is64Bit ? manager.arch64() : manager.arch32();
		manager.setup();
		logger.info("Finished setting up.");
		return creator.apply(options.get());
	}
	
	/**
	 * Centralises building + convenience methods
	 * for options (allows generic methods like
	 * Builder::setProxy).
	 * 
	 * @param <K> - browser options runtime class type
	 */
	public static class Builder<K extends AbstractDriverOptions<?>> {
		protected Supplier<K> options;
		protected Function<K, RemoteWebDriver> creator;
		protected DriverManagerType type;
		
		/* Package private constructor */
		Builder(Supplier<K> options, Function<K, RemoteWebDriver> creator, DriverManagerType type) {
			this.options = options;
			this.creator = creator;
			this.type = type;
			logger.info("Created BrowserConfigurator.Builder: {}", getClass());
		}
		
		public Builder<K> config(Supplier<K> options) {
			logger.info("Configuring option 1: {}", options);
			this.options = options;
			return this;
		}

		public <V> Builder<K> config(Function<K, V> configurator) {
			logger.info("Configuring option 2: {}", options);
			final K ops = options.get();
			configurator.apply(ops);
			this.options = () -> ops;
			return this;
		}
		
		/* Convenience methods */
		
		public Builder<K> setProxy(final String host, final int port) {
			return config(ops -> ops.setProxy(new Proxy().setHttpProxy(String.format("%s:%s", host, port))));
		}
		
		public Builder<K> acceptInsecureCerts(boolean bool) {
			return config(ops -> ops.setAcceptInsecureCerts(bool));
		}
		
		public BrowserConfigurator<K> build() {
			logger.info("Building configurator: {}", getClass());
			return new BrowserConfigurator<>(options, creator, type);
		}
	}
}