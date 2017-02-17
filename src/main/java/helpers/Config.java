package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

public class Config 
{
	public WebDriver driver = null;
	public String testcaseName = "";
	public String testLog = "";
	public SoftAssert softAssert = null;
	public boolean endExecutionOnfailure = false;

	private Properties runTimeProperties = null;
	
	public static HashMap<String, TestDataReader> testDataReaderHashMap = new HashMap<String, TestDataReader>();
	
	
	public Config()
	{
		softAssert = new SoftAssert();
		runTimeProperties = new Properties();
		Properties properties = null;
		
		//Code to read .properties file and put key value pairs into RunTime Property file
		try 
		{
			FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+File.separator+"Parameters"+File.separator+"config.properties");
			properties = new Properties();
			properties.load(fileInputStream);
			fileInputStream.close();
		} 
		catch (Exception e) 
		{
			logComment("=====>>Exception while reading config.properties file...");
			e.printStackTrace();
		}
		
		Enumeration<Object> enumeration = properties.keys();
		while (enumeration.hasMoreElements())
		{
			String str = (String) enumeration.nextElement();
			putRunTimeProperty(str, (String) properties.get(str));
		}
	}
	
	
	/**
	 * Add the given key value pair in the Run Time Properties
	 * 
	 * @param key
	 * @param value
	 */
	public void putRunTimeProperty(String key, String value)
	{
		String keyName = key.toLowerCase();
		runTimeProperties.put(keyName, value);
		logComment("Putting RunTime key-" + keyName + " value:-'" + value + "'");
	}
	
	
	/**
	 * Get the Run Time Property value
	 * 
	 * @param key name whose value is needed
	 * @return value of the specified key
	 */
	public String getRunTimeProperty(String key)
	{
		String keyName = key.toLowerCase();
		String value = "";
		try
		{
			value = runTimeProperties.get(keyName).toString();
			logComment("Reading RunTime key-" + keyName + " value:-'" + value + "'");
		}
		catch (Exception e)
		{
			logComment("'" + key + "' not found in Run Time Properties");
			return null;
		}
		return value;
	}
	
	
	public void logComment(String message)
	{
		Log.Comment(this, message);
	}
	
	public void logWarning(String message)
	{
		Log.Warning(this, message);
	}
	
	public void logWarning(String what, String expected, String actual)
	{
		String message = "Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
		Log.Warning(this, message);
	}
	
	public void logFail(String message)
	{
		Log.Fail(this, message);
	}
	
	public <T> void logFail(String what, T expected, T actual)
	{
		String message = "Expected '" + what + "' was :-'" + expected + "'. But actual is '" + actual + "'";
		Log.Fail(this, message);
	}
	
	public void logPass(String message)
	{
		Log.Pass(this, message);
	}
	
	public <T> void logPass(String what, T actual)
	{
		String message = "Verified '" + what + "' as :-'" + actual + "'";
		Log.Pass(this, message);
	}
	
	
	/**
	 * Get the cached TestDataReader Object for the given sheet. If it is not cached, it will be cached for future use
	 * 
	 * @param sheetName
	 * @return TestDataReader object or null if object is not in cache
	 */
	public TestDataReader getExcelSheet(String sheetName)
	{	
		String path = System.getProperty("user.dir")+File.separator+"Parameters"+File.separator+"TestDataSheet.xls";
		TestDataReader testDataReader = testDataReaderHashMap.get(path + sheetName);
		
		// Object is not in the cache
		if (testDataReader == null)
		{
			// cache for future use
			synchronized(Config.class)
			{
				testDataReader = new TestDataReader(this, sheetName, path);
				testDataReaderHashMap.put(path + sheetName, testDataReader);
			}
		}
		return testDataReader;
	}
}