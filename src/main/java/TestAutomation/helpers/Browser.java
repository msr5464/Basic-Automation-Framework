package TestAutomation.helpers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

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
		String browserVersion = "";
		BrowserName browser = null;
		DesiredCapabilities capabilities = null;
		try {
			browser = BrowserName.valueOf(browserName);
		} catch (IllegalArgumentException e) {
			testConfig.logFail("Invalid Browser name is passed");
		}
		switch (browser) {
		case firefox:
			if (Config.osName.startsWith("Window"))
				System.setProperty("webdriver.gecko.driver", "Drivers" + File.separator + "geckodriver.exe");
			else
				System.setProperty("webdriver.gecko.driver", "Drivers" + File.separator + "geckodriver");
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			driver = new FirefoxDriver(firefoxOptions);
			driver.manage().window().fullscreen();
			break;

		case chrome:
			if (Config.osName.startsWith("Window"))
				System.setProperty("webdriver.chrome.driver", "Drivers" + File.separator + "chromedriver.exe");
			else
				System.setProperty("webdriver.chrome.driver", "Drivers" + File.separator + "chromedriver");
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("disable-infobars");
			chromeOptions.addArguments("start-fullscreen");
			capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
			capabilities.setCapability("version", browserVersion);
			chromeOptions.merge(capabilities);
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
		driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(ObjectWaitTime * 3, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(ObjectWaitTime * 3, TimeUnit.SECONDS);
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