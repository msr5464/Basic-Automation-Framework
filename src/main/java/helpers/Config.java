package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

public class Config {
	public static String projectName;
	public static String environment;
	public static String browserName;
	public static String browserVersion;
	public static String resultsDirectory;
	public static String mobileAppName;
	public static boolean isMobileViewExecution = false;
	public static boolean isMobileAppExecution = false;
	public static boolean isRemoteExecution = true;
	public static boolean isDebugMode = false;
	public static String osName = System.getProperty("os.name");
	public static HashMap<String, TestDataReader> testDataReaderHashMap = new HashMap<String, TestDataReader>();
	public WebDriver driver = null;
	public boolean endExecutionOnfailure = false;
	String testLog = "";
	String testEndTime;
	public String testcaseName;
	public String testcaseClass;
	String testStartTime;
	Connection snapDbConnection = null;
	Connection turboSaudagarDbConnection = null;
	Connection turboPayconDbConnection = null;
	Connection muffinDbConnection = null;
	Connection irisDbConnection = null;
	SoftAssert softAssert = null;
	Properties runTimeProperties = null;
	int testcasesRemaining = 0;
	boolean enableScreenshot = true;
	boolean testResult = true;
	boolean retry = true;

	public Config() {
		softAssert = new SoftAssert();
		runTimeProperties = new Properties();
		Properties properties = null;
		// Code to read .properties file and put key value pairs into RunTime
		// Property file
		try {
			String parametersPath = System.getProperty("user.dir") + File.separator + "Parameters" + File.separator;
			FileInputStream fileInputStream = new FileInputStream(parametersPath + "config.properties");
			properties = new Properties();
			properties.load(fileInputStream);
			fileInputStream.close();

			// override the mobileAppExecution value if passed through
			// TestNG.xml
			if (isMobileAppExecution) {
				if (!StringUtils.isEmpty(mobileAppName))
					properties.put("MobileAppName", mobileAppName);
				fileInputStream = new FileInputStream(
						parametersPath + properties.getProperty("MobileAppName") + ".properties");
				properties.load(fileInputStream);
				fileInputStream.close();
			}

			// override the environment value if passed through TestNG.xml
			if (!StringUtils.isEmpty(environment))
				properties.put("Environment", environment.toLowerCase());
			fileInputStream = new FileInputStream(parametersPath + properties.get("Environment") + ".properties");
			logComment("Running on '" + properties.get("Environment") + "' environment");
			properties.load(fileInputStream);
			fileInputStream.close();
		} catch (Exception e) {
			logComment("Exception while reading config.properties file...");
			e.printStackTrace();
		}

		Enumeration<Object> enumeration = properties.keys();
		while (enumeration.hasMoreElements()) {
			String str = (String) enumeration.nextElement();
			putRunTimeProperty(str, (String) properties.get(str));
		}

		// override param values if passed through TestNG.xml
		if (!StringUtils.isEmpty(resultsDirectory)) {
			putRunTimeProperty("ResultsDirectory", resultsDirectory);
		} else {
			resultsDirectory = System.getProperty("user.dir") + File.separator + "test-output";
			putRunTimeProperty("ResultsDirectory", resultsDirectory);
		}
		if (!StringUtils.isEmpty(projectName))
			putRunTimeProperty("ProjectName", projectName);
		if (!StringUtils.isEmpty(browserName))
			putRunTimeProperty("Browser", browserName);
		if (!StringUtils.isEmpty(browserVersion))
			putRunTimeProperty("BrowserVersion", browserVersion);

		// Putting values into variables from RunTime properties
		endExecutionOnfailure = endExecutionOnfailure
				|| getRunTimeProperty("EndExecutionOnFailure").equalsIgnoreCase("true");
		isMobileViewExecution = isMobileViewExecution
				|| getRunTimeProperty("MobileViewExecution").equalsIgnoreCase("true");
		isMobileAppExecution = isMobileAppExecution
				|| getRunTimeProperty("MobileAppExecution").equalsIgnoreCase("true");
		isRemoteExecution = isRemoteExecution || getRunTimeProperty("RemoteExecution").equalsIgnoreCase("true");
		isDebugMode = isDebugMode || getRunTimeProperty("debugMode").equalsIgnoreCase("true");
		environment = getRunTimeProperty("Environment");
		projectName = getRunTimeProperty("ProjectName");
		mobileAppName = getRunTimeProperty("MobileAppName");
	}

	/**
	 * Add the given key value pair in the Run Time Properties
	 * 
	 * @param key
	 * @param value
	 */
	public void putRunTimeProperty(String key, String value) {
		String keyName = key.toLowerCase();
		runTimeProperties.put(keyName, value);
		logComment("Putting RunTime key-" + keyName + " value:-'" + value + "'");
	}

	/**
	 * Get the Run Time Property value
	 * 
	 * @param key
	 *            name whose value is needed
	 * @return value of the specified key
	 */
	public String getRunTimeProperty(String key) {
		String keyName = key.toLowerCase();
		String value = "";
		try {
			value = runTimeProperties.get(keyName).toString();
			logComment("Reading RunTime key-" + keyName + " value:-'" + value + "'");
		} catch (Exception e) {
			logComment("'" + key + "' not found in Run Time Properties");
			return null;
		}
		return value;
	}

	public void logComment(String message) {
		Log.Comment(this, message);
	}

	public void logWarning(String message) {
		Log.Warning(this, message);
	}

	public void logWarning(String what, String expected, String actual) {
		String message = "Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
		Log.Warning(this, message);
	}

	public void logFail(String message) {
		Log.Fail(this, message);
	}

	public <T> void logFail(String what, T expected, T actual) {
		String message = "Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
		Log.Fail(this, message);
	}

	public void logPass(String message) {
		Log.Pass(this, message);
	}

	public <T> void logPass(String what, T actual) {
		String message = "Verified '" + what + "' as :-'" + actual + "'";
		Log.Pass(this, message);
	}

	/**
	 * Get the cached TestDataReader Object for the given sheet. If it is not
	 * cached, it will be cached for future use
	 * 
	 * @param sheetName
	 * @return TestDataReader object or null if object is not in cache
	 */
	public TestDataReader getExcelSheet(String sheetName) {
		String path = System.getProperty("user.dir") + File.separator + "Parameters" + File.separator
				+ "TestDataSheet.xls";
		TestDataReader testDataReader = testDataReaderHashMap.get(path + sheetName);

		// Object is not in the cache
		if (testDataReader == null) {
			// cache for future use
			synchronized (Config.class) {
				testDataReader = new TestDataReader(this, sheetName, path);
				testDataReaderHashMap.put(path + sheetName, testDataReader);
			}
		}
		return testDataReader;
	}
}