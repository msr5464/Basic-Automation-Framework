package TestAutomation.helpers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * This class will contain all the function related to Browser
 * @author MukeshR
 *
 */
public class Browser {
	private enum BrowserName {
		chrome, firefox, safari, internetExplorer
	};

	public static void openBrowserAndNavigateToUrl(Config testConfig, String url) {
		if (testConfig.driver == null)
			openBrowser(testConfig);

		testConfig.logComment("Navigating to URL : " + url);
		testConfig.driver.get(url);
	}

	private static void openBrowser(Config testConfig) {
		String browserName = testConfig.getRunTimeProperty("browser").toLowerCase().trim();
		testConfig.logComment("Launching '" + browserName + "' browser in local...");
		WebDriver driver = null;
		BrowserName browser = null;
		try {
			browser = BrowserName.valueOf(browserName);
		} catch (IllegalArgumentException e) {
			testConfig.logFail("Invalid Browser name is passed");
		}
		switch (browser) {
		case firefox:
			// Selenium 4.x uses Selenium Manager, no need to set driver path manually
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("--start-fullscreen");
			driver = new FirefoxDriver(firefoxOptions);
			driver.manage().window().fullscreen();
			break;

		case chrome:
			// Selenium 4.x uses Selenium Manager, no need to set driver path manually
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--disable-infobars");
			chromeOptions.addArguments("--start-fullscreen");
			chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
			chromeOptions.addArguments("--disable-extensions");
			chromeOptions.addArguments("--no-sandbox");
			chromeOptions.addArguments("--disable-dev-shm-usage");
			try {
				driver = new ChromeDriver(chromeOptions);
			} catch (WebDriverException e) {
				testConfig.logWarning("[Browser.openBrowser] WebDriverException in first time, so trying again...");
				driver = new ChromeDriver(chromeOptions);
			}
			break;
		default:
			break;
		}
		// Close the browser incase time taken to load a page exceed 2 min
		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ObjectWaitTime));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ObjectWaitTime * 3));
		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(ObjectWaitTime * 3));
		testConfig.driver = driver;
	}

	public static void takeScreenshot(Config testConfig) {
		if (testConfig.driver == null) {
			testConfig.logComment("Driver is NULL, so can't take screenshot!");
		} else {
			File screenshotUrl = getScreenShotFile(testConfig);
			byte[] screenshot = ((TakesScreenshot) testConfig.driver).getScreenshotAs(OutputType.BYTES);
			try {
				FileUtils.writeByteArrayToFile(screenshotUrl, screenshot);
			} catch (IOException e) {
				System.out.println("=====>>Unable to take screenshot...");
				e.printStackTrace();
			}

			String href = CommonUtilities.convertFilePathToHtmlUrl(screenshotUrl.getPath());
			testConfig.logComment(
					"<B>Screenshot</B>:- <a href=" + href + " target='_blank' >" + screenshotUrl.getName() + "</a>");
			testConfig.logComment("<B>Page URL</B>:- <a href=" + testConfig.driver.getCurrentUrl()
					+ " target='_blank' >" + testConfig.driver.getCurrentUrl() + "</a>");
		}

	}

	public static File getScreenShotFile(Config testConfig) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String screenshotName = testConfig.testcaseName + "_" + dateFormat.format(date) + ".png";
		File dest = new File(CommonUtilities.getResultsDirectory(testConfig).getPath() + File.separator + screenshotName);
		return dest;
	}
}