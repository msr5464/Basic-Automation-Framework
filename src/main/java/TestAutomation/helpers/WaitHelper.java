package TestAutomation.helpers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class contains all the functions which can be used to wait for any element on a page
 * @author MukeshR
 *
 */
public class WaitHelper {
	
	/**
	 * This method is created to add static wait for given seconds of time
	 * @param testConfig
	 * @param seconds
	 */
	public static void waitForSeconds(Config testConfig, int seconds){
		int milliseconds = seconds * 1000;
		try{
			testConfig.logComment("Waiting for '" + seconds + "' seconds");
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e){
			testConfig.logExceptionAndFail("", e);
		}
	}

	/**
	 * This method is created to wait for loading of new page,  if passed element will not appear then testcase will fail.
	 * @param testConfig
	 * @param element
	 */
	public static void waitForPageLoad(Config testConfig, WebElement element){
		int objectWaitTime = Integer.parseInt(testConfig.getRunTimeProperty("ObjectWaitTime"));
		waitForPageLoad(testConfig, element, objectWaitTime);
	}
	
	/**
	 * This method is created to wait for loading of new page,  if passed element will not appear then testcase will fail.
	 * @param testConfig
	 * @param element
	 */
	public static void waitForPageLoad(Config testConfig, WebElement element, int objectWaitTimeInSecs){
		double timeTaken = 0;
		String callingClassName = getCallerClassName();
		String currentPageName = callingClassName.substring(callingClassName.lastIndexOf('.') + 1);
		Date startDate = new Date();
		testConfig.logComment("Started waiting for '" + currentPageName + "' to load at:- " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(startDate) + ". Wait upto " + objectWaitTimeInSecs + " seconds.");

		WebDriverWait wait = new WebDriverWait(testConfig.driver, objectWaitTimeInSecs);
		try{
			wait.until(ExpectedConditions.visibilityOf(element));
		}
		catch (StaleElementReferenceException e){
			testConfig.logWarning("StaleElementReferenceException occured, so trying again...");
			try{
				wait.until(ExpectedConditions.visibilityOf(element));
			}
			catch (Exception exc){
				testConfig.logFailToEndExecution("Even after second try, element is not loaded");
			}
		}
		catch (TimeoutException e){
			Date endDate = new Date();
			timeTaken = (endDate.getTime() - startDate.getTime()) / 1000.00;
			testConfig.logFailToEndExecution("'" + currentPageName + "' NOT loaded even after :- " + timeTaken + " seconds.");
		}
		Date endDate = new Date();
		timeTaken = (endDate.getTime() - startDate.getTime()) / 1000.00;
		testConfig.logComment(currentPageName + " loaded in :- " + timeTaken + " seconds.");
		if(timeTaken > 120)
			testConfig.logComment("<B><font color='Red'>" + currentPageName + " is loaded after " + timeTaken/60 + " minutes.</font></B>");
	}

	/**
	 * This method is created to wait for any element, if element will not appear then testcase will fail.
	 * @param testConfig
	 * @param element
	 */
	public static void waitForElement(Config testConfig, WebElement element, String description){
		testConfig.logComment("Started waiting for '"+description+"'");
		WebDriverWait wait = new WebDriverWait(testConfig.driver, Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
		try{
			wait.until(ExpectedConditions.visibilityOf(element));
		}
		catch (StaleElementReferenceException e){
			testConfig.logComment("StaleElementReferenceException occured, so trying again...");
			try{
				wait.until(ExpectedConditions.visibilityOf(element));
			}
			catch (Exception exc){
				testConfig.logFailToEndExecution("Even after second try, element '"+description+"' is not visible");
			}
		}
		catch (TimeoutException e){
			testConfig.logComment("TimeoutException occured, so trying again...");
			try{
				wait.until(ExpectedConditions.visibilityOf(element));
			}
			catch (Exception exc){
				testConfig.logFailToEndExecution("Element '"+description+"' is not visible even after " + testConfig.getRunTimeProperty("ObjectWaitTime") + " seconds");
			}
		}
	}
	
	public static void waitForElementToBeDisplayed(Config testConfig, WebElement element, String description){
		testConfig.logComment("Started waiting for '"+description+"' to be displayed");
		if(element == null){
			testConfig.logFailToEndExecution("Element '" + description + "' is NULL, so can't waitForElementToBeDisplayed !!");
		}
		else {
			WebDriverWait wait = new WebDriverWait(testConfig.driver, 2*Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
			try {
				wait.until(ExpectedConditions.visibilityOf(element));
			}
			catch (StaleElementReferenceException e){
				testConfig.logComment("StaleElementReferenceException occured, so trying again...");
				try {
					wait.until(ExpectedConditions.visibilityOf(element));
				}
				catch (Exception exc){
					testConfig.logFailToEndExecution("Even after second try, element '"+description+"' is not visible");
				}
			}
			catch(TimeoutException exc){
				testConfig.logFailToEndExecution("Element '" + description + "' is visible even after waiting for "+ (2*+Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")) + " seconds"));
			}
		}
	}
	
	public static void waitForElementToBeHidden(Config testConfig, WebElement element, String description){
		testConfig.logComment("Started waiting for '"+description+"' to be hidden");
		WebDriverWait wait = new WebDriverWait(testConfig.driver, 2*Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
		List<WebElement> elements = Arrays.asList(element);
		if(element!=null)
		{
			try {
				wait.until(ExpectedConditions.invisibilityOfAllElements(elements));
			}
			catch(StaleElementReferenceException e){
				testConfig.logComment("StaleElementReferenceException occured, so trying again...");
				try {
					wait.until(ExpectedConditions.invisibilityOfAllElements(elements));
				}
				catch(Exception exc){
					testConfig.logFailToEndExecution("Even after second try, element '"+description+"' is still visible");
				}
			}
			catch(TimeoutException exc){
				testConfig.logFailToEndExecution("Element '" + description + "' is hidden even after waiting for "+ (2*+Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")) + " seconds"));
			}
		}
	}
	
	public static void waitForElementAttributeToBe(Config testConfig, WebElement element, String attributeName, String attributeValue, String description){
		testConfig.logComment("Started waiting for '"+description+"' to have '"+attributeName+"'="+attributeValue);
		WebDriverWait wait = new WebDriverWait(testConfig.driver, 2*Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
		try {
			wait.until(ExpectedConditions.attributeToBe(element, attributeName, attributeValue));
		}
		catch (StaleElementReferenceException e){
			testConfig.logComment("StaleElementReferenceException occured, so trying again...");
			try {
				wait.until(ExpectedConditions.attributeToBe(element, attributeName, attributeValue));
			}
			catch(Exception exc){
				testConfig.logFailToEndExecution("Even after second try, element is still not having '"+attributeName+"'="+attributeValue+"");
			}
		}
		catch(TimeoutException exc){
			testConfig.logFailToEndExecution("Element '" + description + "' is having '"+attributeName+"'="+attributeValue+", after waiting for "+ (2*+Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")) + " seconds"));
		}
	}
	
	public static boolean waitForIframeAndSwitch(Config testConfig, String iframeId){
		testConfig.logComment("Wait for iframe '"+iframeId+"' and switch");
		testConfig.driver.switchTo().defaultContent();
		WebDriverWait wait = new WebDriverWait(testConfig.driver, 2*Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
		try {
			int counter = Integer.parseInt(iframeId);
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(counter));
		}
		catch(NumberFormatException e){
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeId));
		}
		catch(TimeoutException exc){
			testConfig.logFailToEndExecution("Iframe is not loaded even after waiting for "+ (2*+Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")) + " seconds"));
			return false;
		}
		return true;
	}
	
	public static void waitForElementToBeClickable(Config testConfig, WebElement element, String description){
		testConfig.logComment("Started waiting for '"+description+"' to be clickable");
		WebDriverWait wait = new WebDriverWait(testConfig.driver, Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
		try{
			wait.until(ExpectedConditions.elementToBeClickable(element));
		}
		catch (StaleElementReferenceException e){
			testConfig.logComment("StaleElementReferenceException occured, so trying again...");
			try{
				wait.until(ExpectedConditions.elementToBeClickable(element));
			}
			catch (Exception exc){
				testConfig.logFailToEndExecution("Even after second try, element '"+description+"' is not loaded");
			}
		}
	}

	private static String getCallerClassName(){
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++){
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(WaitHelper.class.getName()) && !ste.getClassName().contains("Helper") && ste.getClassName().indexOf("java.lang.Thread") != 0)
				return ste.getClassName();
		}
		return null;
	}
	
	/**
	 * This method is created to wait for any element, but if element will not appear then also testcase will NOT fail.
	 * @param testConfig
	 * @param element
	 */
	public static void waitForOptionalElement(Config testConfig, WebElement element, String description){
		testConfig.logComment("Started waiting for '"+description+"'");
		WebDriverWait wait = new WebDriverWait(testConfig.driver, Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
		try{
			wait.until(ExpectedConditions.visibilityOf(element));
		}
		catch (StaleElementReferenceException e){
			testConfig.logComment("StaleElementReferenceException occured, so trying again...");
			try{
				wait.until(ExpectedConditions.visibilityOf(element));
			}
			catch (TimeoutException exc){
				testConfig.logWarning("Even after second try, element is not visible.");
			}
		}
		catch (TimeoutException exc){
			testConfig.logWarning("Optional element '"+description+"' is not visible!");
		}
	}
}