package helpers;

import org.testng.Assert;
import org.testng.Reporter;

class Log
{
	public static void Comment(Config testConfig, String message, String color)
	{
		logToStandard(message);
		message = "<font color='" + color + "'>" + message + "</font></br>";
		Reporter.log(message);
		testConfig.testLog = testConfig.testLog.concat(message);
	}
	
	public static void Comment(Config testConfig, String message)
	{
		Comment(testConfig, message, "Black");
	}
	
	public static void Fail(Config testConfig, String message)
	{
		PageInfo(testConfig);
		failure(testConfig, message);
	}
	
	public static void failure(Config testConfig, String message)
	{
		String tempMessage = message;
		testConfig.softAssert.fail(message);

		logToStandard(message);
		message = "<font color='Red'>" + message + "</font></br>";
		Reporter.log(message);
		testConfig.testLog = testConfig.testLog.concat(message);
		
		// Stop the execution if end execution flag is ON
		if (testConfig.endExecutionOnfailure)
			Assert.fail("=====>>Ending execution in the middle:-"+tempMessage);
	}
	
	public static void Failfinal(Config testConfig, String message)
	{
		try
		{
			PageInfo(testConfig);
		}
		catch (Exception e)
		{
			testConfig.logWarning("Unable to log page info:- ");
			e.printStackTrace();
		}
	}
	
	private static void logToStandard(String message)
	{
		System.out.println(message);
	}
	
	private static void PageInfo(Config testConfig)
	{
		if (testConfig.driver != null)
		{
			Browser.takeScreenshot(testConfig);
		}
	}
	
	public static void Pass(Config testConfig, String message)
	{
		logToStandard(message);
		message = "<font color='Green'>" + message + "</font></br>";
		Reporter.log(message);
		testConfig.testLog = testConfig.testLog.concat(message);
	}
	
	public static void Warning(Config testConfig, String message)
	{
		logToStandard(message);
		message = "<font color='Orange'>" + message + "</font></br>";
		Reporter.log(message);
		testConfig.testLog = testConfig.testLog.concat(message);
	}
	
	public static void Warning(Config testConfig, String message, boolean logPageInfo)
	{
		if (logPageInfo)
			PageInfo(testConfig);
		
		Warning(testConfig, message);
	}
}