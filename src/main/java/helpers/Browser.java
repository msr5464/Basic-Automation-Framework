package helpers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

	@SuppressWarnings("deprecation")
	private static void openBrowser(Config testConfig) {
		String mobileUserAgent = "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 6P Build/MTC19X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.81 Mobile Safari/537.36";
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
			// Creating a new Firefox Profile
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			if (Config.isMobileViewExecution)
				firefoxOptions.addPreference("general.useragent.override", mobileUserAgent);

			driver = new FirefoxDriver(firefoxOptions);
			if (Config.isMobileViewExecution) {
				driver.manage().window().setPosition(new Point(0, 0));
				driver.manage().window().setSize(new Dimension(360, 640));
			} else {
				driver.manage().window().fullscreen();
			}
			break;

		case chrome:
			if (Config.osName.startsWith("Window"))
				System.setProperty("webdriver.chrome.driver", "Drivers" + File.separator + "chromedriver.exe");
			else
				System.setProperty("webdriver.chrome.driver", "Drivers" + File.separator + "chromedriver");
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("disable-infobars");
			if (testConfig.getRunTimeProperty("HeadlessExecution").equalsIgnoreCase("true"))
				chromeOptions.addArguments("--headless");
			if (Config.isMobileViewExecution) {
				Map<String, String> mobileEmulation = new HashMap<String, String>();
				mobileEmulation.put("deviceName", "Galaxy Note 3");
				chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
				chromeOptions.addArguments("--window-size=360,640");
			} else {
				chromeOptions.addArguments("start-fullscreen");
			}
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

		case safari:
			System.setProperty("webdriver.safari.driver", "Drivers" + File.separator + "SafariDriver");
			SafariOptions safariOptions = new SafariOptions();
			// safariOptions.setUseCleanSession(true);
			safariOptions.setCapability("cleanSession", true);
			capabilities = DesiredCapabilities.safari();
			capabilities.setCapability(SafariOptions.CAPABILITY, safariOptions);
			capabilities.setCapability("version", browserVersion);
			driver = new SafariDriver(capabilities);
			break;

		case internetExplorer:
			System.setProperty("webdriver.ie.driver", "Drivers" + File.separator + "IEDriverServer");
			capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability("ignoreProtectedModeSettings", true);
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			capabilities.setCapability("version", browserVersion);
			driver = new InternetExplorerDriver(capabilities);
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

	public static void wait(Config testConfig, int seconds) {
		int milliseconds = seconds * 1000;
		try {
			Thread.sleep(milliseconds);
			testConfig.logComment("Wait for '" + seconds + "' seconds");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void waitForPageLoad(Config testConfig, WebElement element) {
		WebDriverWait wait = new WebDriverWait(testConfig.driver, Long.parseLong("30"));

		try {
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (StaleElementReferenceException e) {
			testConfig.logComment("StaleElementReferenceException occured, so trying again...");

			try {
				wait.until(ExpectedConditions.visibilityOf(element));
			} catch (Exception exc) {
				testConfig.logComment("Even after second try, element is not loaded, so exiting.");
			}
		}

		testConfig.logComment("Page is successfully loaded.");
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

			String href = convertFilePathToHtmlUrl(screenshotUrl.getPath());
			testConfig.logComment(
					"<B>Screenshot</B>:- <a href=" + href + " target='_blank' >" + screenshotUrl.getName() + "</a>");
			testConfig.logComment("<B>Page URL</B>:- <a href=" + testConfig.driver.getCurrentUrl()
					+ " target='_blank' >" + testConfig.driver.getCurrentUrl() + "</a>");
		}

	}

	private static File getResultsDirectory(Config testConfig) {
		File dest = new File(System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "html"
				+ File.separator);
		return dest;
	}

	public static File getScreenShotFile(Config testConfig) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String screenshotName = testConfig.testcaseName + "_" + dateFormat.format(date) + ".png";
		File dest = new File(getResultsDirectory(testConfig).getPath() + File.separator + screenshotName);
		return dest;
	}

	/**
	 * This function return the URL of a file on runtime depending on LOCAL or
	 * OFFICIAL Run
	 * 
	 * @param testConfig
	 * @param fileUrl
	 * @return
	 */
	public static String convertFilePathToHtmlUrl(String fileUrl) {
		String htmlUrl = "";
		htmlUrl = fileUrl.replace(File.separator, "/");
		;

		return htmlUrl;
	}
}