package lib.scrape;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/* Creates a browser using selenium that follows the builder pattern.
 * 
 * Docs: https://www.selenium.dev/documentation/en/webdriver/   
 */
public class Browser {
	private static final long DEFAULT_TIMEOUT = 15, DEFAULT_POLLING = 5;	// in seconds
	
	private WebDriver driver;
	
	static {
		WebDriverManager.firefoxdriver().arch64().setup();	// Makes sure firefox driver exists or downloads it
	}
	
	public Browser() {
		this(false);
	}
	
	public Browser(boolean headless) {
		driver = new FirefoxDriver(
				new FirefoxOptions()
					.addArguments(	// comment as needed
							headless ? "--headless" : "", 
							"--disable-gpu", 
							"--window-size=1920,1200",
							"--ignore-certificate-errors"
					));
	}
	
	public void close() {
		driver.quit();
	}
	
	public String getTitle() {
		return driver.getTitle();
	}
	
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}
	
	public Dimension getSize() {
		return driver.manage().window().getSize();
	}
	
	public Browser setSize(int width, int height) {
		driver.manage().window().setSize(new Dimension(width, height));
		return this;
	}
	
	public Browser back() {
		driver.navigate().back();
		return this;
	}
	
	public Browser forward() {
		driver.navigate().forward();
		return this;
	}
	
	public Browser refresh() {
		driver.navigate().refresh();
		return this;
	}
	
	public Browser then(Consumer<WebDriver> consumer) {
		consumer.accept(driver);
		return this;
	}
	
	public <R> R get(Function<WebDriver, R> mapper) {
		return mapper.apply(driver);
	}
	
	public Browser visit(String url) {
		driver.navigate().to(url);
		return this;
	}
	
	public File screenshot() {
		return ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	}
	
	public WebElement waitFor(By by, long timeout) {
		return new WebDriverWait(driver, timeout)
				.until(driver -> driver.findElement(by));
	}
	
	public WebElement waitFor(By by, long timeout, long polling, Collection<Class<? extends Throwable>> exceptions) {
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeout))
				.pollingEvery(Duration.ofSeconds(polling))
				.ignoreAll(exceptions)
				.until(driver -> driver.findElement(by));
	}
	
	public WebElement waitFor(By by) {
		return waitFor(by, DEFAULT_TIMEOUT);
	}
	
	public WebElement waitFor(By by, long timeout, long polling, Class<? extends Throwable> exception) {
		return waitFor(by, timeout, polling, Arrays.asList(exception));
	}
	
	public WebElement waitFor(By by, long timeout, long polling) {
		return waitFor(by, timeout, polling, NoSuchElementException.class);
	}
	
	public WebElement waitForFluent(By by) {
		return waitFor(by, DEFAULT_TIMEOUT, DEFAULT_POLLING);
	}
}
