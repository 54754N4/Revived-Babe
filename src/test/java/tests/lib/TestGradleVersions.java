package tests.lib;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;

import annotations.SlowTest;
import lib.scrape.Browser;
import lib.scrape.Dependency;

public class TestGradleVersions {

	@Disabled("Scraper gets blocked by Captcha")
	@SlowTest
    public void testGradleDependencies_latestVersion() throws Exception {
        Dependency.checkUpdates(
        	(name, latest, updated) -> 
        		assertTrue(updated, String.format("%s is not updated to latest %s", name, latest)));
    }
	
	@AfterAll
	public static void preventBrowsersMemoryLeak() {
		Browser.getInstance().close();
	}

}
