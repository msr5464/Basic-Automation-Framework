package helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Function;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;

public class MobileActions extends Element
{
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be checked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void check(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Check '" + description + "'");
		if (getAttribute(testConfig, element, "checked").equals("false"))
		{
			try
			{
				element.click();
			}
			catch (StaleElementReferenceException e)
			{
				element.click();
			}
		}
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be checked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */	
	public static void click(Config testConfig, WebElement element, String description)
	{
		try
		{
			Element.click(testConfig, element, description);

		}
		catch (NoSuchElementException e)
		{
			testConfig.logComment("Unable to locate element. Scroll Down and try again");
			swipeBottomToTop(testConfig, 0.5);
			try
			{
				Element.click(testConfig, element, description);
			}
			catch (NoSuchElementException nse)
			{
				testConfig.logComment("Unable to locate element. Scroll Up and try again");
				swipeTopToBottom(testConfig, 0.5);
				Element.click(testConfig, element, description);
			}
		}
	}
	
	/** This is used for clicking on Web View Elements which does not get clicked via Normal click
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be checked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */	
	public static void clickWebViewElements(Config testConfig, WebElement element, String description)
	{
		click(testConfig, element, description);
		testConfig.appiumDriver.getKeyboard().sendKeys(Keys.ENTER);
		
	}
	
	
	/**
	 * Enters the given 'value'in the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void enterData(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			testConfig.logComment("Enter the " + description + " as '" + message + "'");
			
			if(!enterDataInEditField(testConfig, element, value, null))
			{
				hideKeyboard(testConfig);
				testConfig.logComment("Unable to locate element. Scroll Down and try again");
				swipeBottomToTop(testConfig, 0.5);
				if(!enterDataInEditField(testConfig, element, value, null))
				{
					hideKeyboard(testConfig);
					testConfig.logComment("Unable to locate element. Scroll up and try again");
					swipeTopToBottom(testConfig, 0.5);
					enterDataInEditField(testConfig, element, value, null);
				}
			}
		}
		else
		{
			testConfig.logComment("Skipped data entry for " + description);
		}
	}
	
	/**
	 * Enter data in edit field
	 * @param testConfig
	 * @param element
	 * @param value
	 * @return true/false
	 */
	private static boolean enterDataInEditField(Config testConfig, WebElement element, String value, WebElement elementToClick)
	{
		boolean valueEntered = false;
		try
		{
			element.clear();
			element.sendKeys(value);
			
			if(elementToClick != null)
			{
				try
				{
					click(testConfig, elementToClick, "Hide keyboard button");
				}
				catch (org.openqa.selenium.WebDriverException e)
				{
					testConfig.logComment("Looks like keyboard is already hidden.Do Nothing");
				}
			}
			else
				hideKeyboard(testConfig);
			valueEntered = true;
		}
		catch(NoSuchElementException e)
		{
			valueEntered = false;
		}
		return valueEntered;
	}
		
	/**
	 * Enters the given 'value'in the specified WebElement without clearing the text
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 *            
	 *            
	 */
	
	public static void enterDataWithoutClearingText(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			try
			{
					element.sendKeys(value);
					hideKeyboard(testConfig);
			}
			catch(NoSuchElementException e)
			{
				
				testConfig.logComment("Unable to locate element. Scroll Down and try again");
				swipeBottomToTop(testConfig, 0.5);
				element.clear();
				element.sendKeys(value);
				hideKeyboard(testConfig);
				
			}
		}
		else
		{
			testConfig.logComment("Skipped data entry for " + description);
		}
	}
	/**
	 * This method has been overloaded because in IOS appium.hidekeyboard() function does not work.
	 * @param testConfig Config instance used for logging
	 * @param element WebElement where data needs to be entered
	 * @param value value to the entered
	 * @param description logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void enterDataNoKeyboard(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			testConfig.logComment("Enter the " + description + " as '" + message + "'");
			try
			{
				element.clear();
				element.sendKeys(value);
			}
			catch(NoSuchElementException e)
			{
				testConfig.logComment("Unable to locate element. Scroll Down and try again");
				swipeBottomToTop(testConfig, 0.5);
				element.clear();
				element.sendKeys(value);
			}
		}
		else
		{
			testConfig.logComment("Skipped data entry for " + description);
		}
	}
	/**
	 * Finds Element by id
	 * 
	 * @param testConfig
	 * @param id
	 *            - id/resource-id of element
	 * @return
	 */
	public static WebElement findElementById(Config testConfig, String id)
	{
		testConfig.logComment("Finding element by id :" + id);
		WebElement elementFound = testConfig.appiumDriver.findElementById(id);
		return elementFound;
	}
	
	/**
	 * This Method is used to fetch text of an element and return the same
	 * @param testConfig -- Config Instance Used for Logging
	 * @param Element	 -- Element Whose text we need to locate
	 * 
	 * @return			 -- Text of the Element
	 */
	public static String getText(Config testConfig,WebElement Element)
	{
		String text= null;
		try
		{
			text = Element.getText();
		}
		catch(NoSuchElementException e)
		{
			testConfig.logComment("Unable to locate element. Scroll Down and try again");
			swipeBottomToTop(testConfig, 0.5);
			text = Element.getText();
		}
		return text; 
	}
	
	/**
	 * Get Mobile Element Within Another Element
	 * @param testConfig
	 * @param element - Element in which another element need to search
	 * @param how - How to search element
	 * @param what - What properties need to search
	 * @return WebElement
	 */
	public static WebElement getMobileElementWithinAnotherElement(Config testConfig, WebElement element, How how, String what)
	{
		WebElement mobileElement = Element.getElementWithinAnotherElement(testConfig, element, how, what);
		if(mobileElement == null)
		{
			testConfig.logComment("Unable to locate element. Scroll Down and try again");
			swipeBottomToTop(testConfig, 0.5);
			mobileElement = Element.getElementWithinAnotherElement(testConfig, element, how, what);
		}
		
		if(mobileElement == null)
		{
			testConfig.logComment("Unable to locate element. Scroll up and try again");
			swipeTopToBottom(testConfig, 0.5);
			mobileElement = Element.getElementWithinAnotherElement(testConfig, element, how, what);
		}
		
		return mobileElement;
	}
	
	/**
	 * Get value of given attribute
	 * @param testConfig
	 * @param Element
	 * @param attribute
	 * @return attributeValue
	 */
	public static String getAttribute(Config testConfig, WebElement Element, String attribute)
	{
		String text= null;
		try
		{
			text = Element.getAttribute(attribute);
		}
		catch(NoSuchElementException e)
		{
			try
			{
			testConfig.logComment("Unable to locate element. Scroll Down and try again");
			swipeBottomToTop(testConfig, 0.5);
			text = Element.getAttribute(attribute);
			}
			catch(NoSuchElementException ee)
			{
				testConfig.logComment("Unable to locate element. Scroll Up and try again");
				swipeTopToBottom(testConfig, 0.5);
				text = Element.getAttribute(attribute);
			}
		}
		return text; 
	}
	
	/**
	 * Find elements by classname
	 * 
	 * @param testConfig
	 * @param className
	 *            class attribute of element
	 * @return list of elements
	 */
	public static List<MobileElement> findElementsByClassName(Config testConfig, String className)
	{
		testConfig.logComment("Finding element by className " + className);
		return testConfig.appiumDriver.findElementsByClassName(className);
	}
	
	/**
	 * Find elements by id
	 * 
	 * @param testConfig
	 * @param id
	 *            id attribute of element
	 * @return list of elements
	 */
	public static List<MobileElement> findElementsById(Config testConfig, String id)
	{
		testConfig.logComment("Finding element by id " + id);
		List<MobileElement> listOfElements = new ArrayList<>();
		int retry = 0;
		while (listOfElements.size() == 0 && retry++ < 5)
		{
			listOfElements = testConfig.appiumDriver.findElementsById(id);
			Browser.wait(testConfig, 3);
		}
		return listOfElements;
	}
	
	/**
	 * Find elements by xpath
	 * 
	 * @param testConfig
	 * @param className
	 *            class attribute of element
	 * @return list of elements
	 */
	public static List<MobileElement> findElementsByXpath(Config testConfig, String xpath)
	{
		testConfig.logComment("Finding element by Xpath " + xpath);
		List<MobileElement> listOfElements = new ArrayList<>();
		int retry = 0;
		while (listOfElements.size() == 0 && retry++ < 3)
		{
			listOfElements = testConfig.appiumDriver.findElementsByXPath(xpath);
			Browser.wait(testConfig, 3);
		}
		return listOfElements;
	}
	
	/**
	 * Hides Keyboard
	 * 
	 * @param testConfig
	 */
	public static void hideKeyboard(Config testConfig)
	{
		testConfig.logComment("Hiding Keyboard");
		try
		{
			if(testConfig.iosDriver != null)
				testConfig.iosDriver.getKeyboard().sendKeys(Keys.RETURN);
			else
				testConfig.appiumDriver.hideKeyboard();

			Browser.wait(testConfig, 1);
		}
		catch (org.openqa.selenium.WebDriverException e)
		{
			testConfig.logComment("Looks like keyboard is already hidden.Do Nothing");
		}
	}
	
	/**
	 * Scrolls To Option with specified text and selects it
	 * 
	 * @param testConfig
	 *            for logging
	 * @param optionText
	 *            For finding specified option
	 */
	public static void selectFromListView(Config testConfig, String optionText)
	{
		HashMap<String, String> scrollObject = new HashMap<String, String>();
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		
		RemoteWebElement listElement = (RemoteWebElement) testConfig.driver.findElement(By.className("android.widget.ListView"));
		String listId = ((RemoteWebElement) listElement).getId();
		scrollObject.put("text", optionText);
		scrollObject.put("element", listId);
		scrollObject.put("direction", "up");
		WebElement listOption = null;
		try
		{
			listOption = testConfig.driver.findElement(By.xpath("//*[@text='" + optionText + "']"));
		}
		catch(Exception ex)
		{
			testConfig.logComment("Option - \"" + optionText + "\" is not found");
		}
		
		try
		{
			if(listOption == null)
			{
				testConfig.logComment("Scrolling List To Find " + optionText);
				js.executeScript("mobile: scrollTo", scrollObject);
				
				listOption = testConfig.driver.findElement(By.xpath("//*[@text='" + optionText + "']"));
			}
		}
		catch(Exception ex)
		{
			testConfig.logFail("Option - \"" + optionText + "\" is still not found");
		}
		
		try
		{
			if(listOption == null)
			{
				scrollObject.put("direction", "down");
				testConfig.logComment("Scrolling List To Find " + optionText);
				js.executeScript("mobile: scrollTo", scrollObject);
				
				listOption = testConfig.driver.findElement(By.xpath("//*[@text='" + optionText + "']"));
			}
			click(testConfig, listOption, optionText);
		}
		catch(Exception ex)
		{
			testConfig.logFail("Option - \"" + optionText + "\" is still not found");
			goBack(testConfig);
		}
		
	}
	
	/**
	 * Send keys by pressing keys on keyboard Note : Only lowercase alphabets
	 * can be sent as of now
	 * 
	 * @param testConfig
	 * @param stringToBeEntered
	 *            - String to be typed
	 */
	public static void sendKeysUsingKeyboard(Config testConfig, String stringToBeEntered)
	{
		
		int keyCodeFora = 29;
		int keyCodeFor0 = 7;
		int code = 0;
		
		testConfig.logComment("Sending keys using Events for string " + stringToBeEntered);
		char[] keys = stringToBeEntered.toLowerCase().toCharArray();
		
		for (char c : keys)
		{
			if (Character.isAlphabetic(c))
			{
				code = keyCodeFora + Character.compare(c, 'a');
			}
			else
				if (Character.isDigit(c))
				{
					code = keyCodeFor0 + Character.compare(c, '0');
				}
				else
					if (c == '@')
					{// Add more special characters if required
						code = 77;
					}
					else
						if (c == '.')
						{
							code = 56;
						}
			
			// testConfig.logComment("Sending code " + code);//Comment out if
			// required
			testConfig.androidDriver.pressKeyCode(code);
		}
		hideKeyboard(testConfig);
	}
	
	/**
	 * Swipes on screen. Example : swipe(0.1,0.5,0.5,0.5,500)
	 * 
	 * @param testConfig
	 * @param startX
	 *            Relative start point for x.Assuming horizontal length on scale
	 *            of 0 to 1
	 * @param startY
	 *            Relative start point for y.Assuming vertical length on scale
	 *            of 0 to 1
	 * @param endX
	 *            Relative end point for x.Assuming horizontal length on scale
	 *            of 0 to 1
	 * @param endY
	 *            Relative end point for y.Assuming vertical length on scale of
	 *            0 to 1
	 * @param duration
	 *            duration for swipe
	 */
	public static  void swipe(Config testConfig, double startX, double startY, double endX, double endY, int duration)
	{
		Dimension dimension = testConfig.driver.manage().window().getSize();
		int height = dimension.height;
		int width = dimension.width;
		int sX = (int) (startX * width);
		int eX = (int) (endX * width);
		int sY = (int) (startY * height);
		int eY = (int) (endY * height);
		try
		{
			testConfig.appiumDriver.swipe(sX, sY, eX, eY, duration);
		}
		catch(WebDriverException wde)
		{
			testConfig.logWarning("Some error occured in swipe");
		}
		testConfig.logComment("Swiped From (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
	}
	
	/**
	 * Swipes from bottom to top
	 * 
	 * @param testConfig
	 * @param duration
	 *            - Time to swipe
	 * @param horizontalPosition
	 *            Horizontal position on screen
	 */
	public static void swipeBottomToTop(Config testConfig, double horizontalPosition)
	{
		swipe(testConfig, horizontalPosition, 0.8, horizontalPosition, 0.2, 500);
	}
	
	/**
	 * Swipes from left to right starting horizontally from left to right
	 * 
	 * @param testConfig
	 * @param duration
	 *            - Time to swipe
	 * @param verticalPosition
	 *            Vertical position on screen
	 */
	public static void swipeLeftToRight(Config testConfig, double verticalPosition)
	{
		swipe(testConfig, 0.15, verticalPosition, 0.9, verticalPosition, 500);
	}
	
	/**
	 * Swipes from right to left starting horizontally from right to left
	 * 
	 * @param testConfig
	 * @param duration
	 *            - Time to swipe
	 * @param verticalPosition
	 *            Vertical position on screen
	 */
	public static void swipeRightToLeft(Config testConfig, double verticalPosition)
	{
		swipe(testConfig, 0.9, verticalPosition, 0.15, verticalPosition, 500);
	}
	
	/**
	 * Swipes from top to bottom
	 * 
	 * @param testConfig
	 * @param duration
	 *            - Time to swipe
	 * @param horizontalPosition
	 *            Horizontal position on screen
	 */
	public static void swipeTopToBottom(Config testConfig, double horizontalPosition)
	{
		swipe(testConfig, horizontalPosition, 0.3, horizontalPosition, 0.8, 500);
	}
	
	/**
	 * @param testConfig
	 *            for logging purpose
	 * @param relativeX
	 *            - horizontal point on screen
	 * @param relativeY
	 *            - vertical point on screen
	 */
	public static void tap(Config testConfig, double relativeX, double relativeY)
	{
		int fingers = 1;// Taken 1 as default
		int duration = 5;// time taken as default
		tap(testConfig, fingers, relativeX, relativeY, duration);
	}
	
	/**
	 * @param testConfig
	 *            for logging purpose
	 * @param fingers
	 *            - number of fingers for tapping
	 * @param relativeX
	 *            - relative horizontal point on screen
	 * @param relativeY
	 *            - relative vertical point on screen
	 * @param duration
	 *            - duration till point has to be tapped
	 */
	public static void tap(Config testConfig, int fingers, double relativeX, double relativeY, int duration)
	{
		Dimension dimension = testConfig.driver.manage().window().getSize();
		int height = dimension.height;
		int width = dimension.width;
		int X = (int) (relativeX * width);
		int Y = (int) (relativeY * height);
		testConfig.appiumDriver.tap(fingers, X, Y, duration);
		testConfig.logComment("Tapped on point (" + X + "," + Y + ") with " + fingers + " for " + duration + " seconds");
		
	}
	
	/**
	 * @param testConfig
	 * @param fingers
	 * @param elementToBeTapped
	 * @param duration
	 * @param description
	 */
	private static void tap(Config testConfig, int fingers, WebElement elementToBeTapped, int duration, String description)
	{
		
		testConfig.appiumDriver.tap(fingers, elementToBeTapped, duration);
		testConfig.logComment("Tapped on " + description + " with " + fingers + " for " + duration + " seconds");
		
	}
	
	/**
	 * @param testConfig
	 * @param elementToBeTapped
	 *            - Element that is to be tapped
	 * @param description
	 *            - for logging purpose
	 */
	public static void tap(Config testConfig, WebElement elementToBeTapped, String description)
	{
		int fingers = 1;// Assuming that only one finger is required
		int duration = 5;// Duration of 5 milliseconds
		tap(testConfig, fingers, elementToBeTapped, duration, description);
	}
	
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be unchecked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void uncheck(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Un-Check '" + description + "'");
		if (getAttribute(testConfig, element, "checked").equals("true"))
		{
			try
			{
				element.click();
			}
			catch (StaleElementReferenceException e)
			{
				element.click();
			}
		}
	}
	
	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param Config
	 *            test config instance for the driver instance on which element
	 *            is to be searched
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @param timeInSeconds
	 *            Polling time
	 */
	public static void waitForVisibility(Config testConfig, WebElement element, int timeInSeconds, String description)
	{
		testConfig.logComment("Wait for element '" + description + "' to be visible on the page.");
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(testConfig.driver).withTimeout(timeInSeconds, TimeUnit.SECONDS).pollingEvery(timeInSeconds / 4, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		
		try
		{
			wait.until(ExpectedConditions.visibilityOf(element));
			testConfig.logComment("Element '" + description + "' is visible now");
		}
		catch (TimeoutException tm)
		{
			throw new TimeoutException(description + " not found after waiting for " + timeInSeconds + " seconds");
		}
	}
	
	/**
	 * Wait for element to be visible on the page
	 * 
	 * @param Config
	 *            test config instance for the driver instance on which element
	 *            is to be searched
	 * @param element
	 *            element to be searched
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void waitForVisibility(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Wait for element '" + description + "' to be visible on the page.");
		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(testConfig.driver).withTimeout(ObjectWaitTime, TimeUnit.SECONDS)
				.pollingEvery(ObjectWaitTime / 4, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
		try
		{
			wait.until(ExpectedConditions.visibilityOf(element));
			testConfig.logComment("Element is visible now.");
		}
		catch (TimeoutException tm)
		{
			throw new TimeoutException(description + " not found after waiting for " + ObjectWaitTime + " seconds");
		}
		catch (WebDriverException we)
		{
			Throwable cause = we.getCause();
			if(cause != null && cause.toString().contains("java.lang.InterruptedException"))
			{
				//testConfig.logComment("Interrupted exception occured. Retrying ....");
				waitForVisibility(testConfig, element, description);
			}
		}
	}

	/**
	 * Wait for an element until having given text
	 * @param testConfig
	 * @param element
	 * @param expectedText
	 */
	public static void waitForElementToHaveGivenText(Config testConfig, WebElement element, String expectedText)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date startDate = new Date();

		Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
		testConfig.logComment("Started waiting for text '" + expectedText + "' at time " + dateFormat.format(startDate));

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(testConfig.driver);
		wait.withTimeout(ObjectWaitTime, TimeUnit.SECONDS);
		wait.pollingEvery(1, TimeUnit.SECONDS);
		wait.ignoring(NoSuchElementException.class);
		try
		{
			wait.until(new Function<WebDriver, Boolean>()
			{
				public Boolean apply(WebDriver webDriver)
				{
					String text = element.getText().toLowerCase();
					if(text!=null && text.equals(expectedText.toLowerCase()))
						return true;
					else
						return false;
				}
			});

			Date endDate = new Date();
			double timeTaken = (endDate.getTime() - startDate.getTime()) / 1000.00;
			testConfig.logComment("Text '" + expectedText + "' found in :- " + timeTaken + " seconds.");
		}
		catch(TimeoutException e)
		{
			e.printStackTrace();
			testConfig.logWarning("'" + expectedText + "' not found");
		}
	}
	
	/**
	 * Returns the WebElement by classname
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 *             @param className
	 *            Class name of the webElement
	 */

	public static WebElement findElementByClassName(Config testConfig, String className)
	{
		testConfig.logComment("Finding element by className :" + className);
		WebElement elementFound = testConfig.appiumDriver.findElementByClassName(className);
		return elementFound;
	}
	
	/**
	 * Select a value from the list of elements
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param className          
	 *            Common Class name of the webElement in the container
	 * @param id
	 * 			Id of the container
	 *  
	 * @param valueToBeSelected
	 *   		value to be selected from the container list
	 *
	 *
	 */
	
	public static void selectFromList(Config testConfig, String className, String id, String valueToBeSelected)
	{
		testConfig.logComment("In method selectFromList selecting value from the list");
		try{
		WebElement containerElement = findElementByClassName(testConfig, className);
		 List<WebElement> itemsInContainer = containerElement.findElements(By.id(id));
		 for(WebElement eachItem:itemsInContainer){
			 String eachItemStr = eachItem.getText();
			 
			 if(eachItemStr.equalsIgnoreCase(valueToBeSelected)){
				 click(testConfig, eachItem, "Click on "+eachItemStr);
				 break;
			 }
			
		 }
		}
		catch(NoSuchElementException e){
			testConfig.logFail("No Such element found");
		}
		
		 
	}
	
	/**
	 * return list of elements from a container
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param className          
	 *            Common Class name of the webElement in the container
	 * @param id
	 * 			Id of the container
	 * @return ArrayList<WebElements>
		 */
	public static ArrayList<WebElement> returnList(Config testConfig, String className, String id)
	{
		testConfig.logComment("In method selectFromList selecting value from the list");
		List<WebElement> itemsInContainer = null;
		try{
		WebElement containerElement = findElementByClassName(testConfig, className);
		itemsInContainer = containerElement.findElements(By.id(id));
		}
		catch(NoSuchElementException e){
			testConfig.logFail("No Such element found");
		}
		
		 return (ArrayList<WebElement>) itemsInContainer;
	}
	/**
	 * Go back to previous screen
	 * 
	 * @param testConfig
	 */
	public static void goBack(Config testConfig)
	{
		testConfig.logComment("go back");
		testConfig.appiumDriver.navigate().back();
	}
	
	/**
	 * check if a Webelement is displayed on a page
	 * @param testConfig
	 * @param WebElement
	 * @return true or false accordingly
	 */
	public static boolean isVisible(Config testConfig, WebElement element)
	{
		boolean isVisible = false;
		try{
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			isVisible = element.isDisplayed();
			testConfig.logComment("element "+element+" "+isVisible);
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			isVisible = element.isDisplayed();	
			testConfig.logComment("element "+isVisible);
		}
		catch (NoSuchElementException e)
		{
			isVisible = false;
			testConfig.logComment("element "+isVisible);
		}
		catch (ElementNotVisibleException e)
		{
			isVisible = false;
			testConfig.logComment("element "+isVisible);
		}
		
		return isVisible;
	}
	
	/**
	 * check if a Webelement is displayed on a page by swiping up and down the screen.
	 * @param testConfig
	 * @param WebElement
	 * @return true or false accordingly
	 */
	public static boolean isDisplayed(Config testConfig, WebElement element)
	{	
		boolean found =false;
		int maxCount=1;
		int count=0;
		while(maxCount>=0)
		{
			 if(isVisible(testConfig,element))
			 {
				 found=true;
				 break;
			 }
			 swipe(testConfig, 0.5, 0.8, 0.5, 0.55, 1400);
			 count++;
			 if(count==10)
			 {
				 maxCount--;
				 count=0;
				 MobileActions.swipeTopToBottom(testConfig,0.1);
			 }
		}
			return found;		
	}
	
	/**
	 * Enter data and click on given button
	 * @param testConfig
	 * @param element
	 * @param value
	 * @param description
	 * @param buttonToClick
	 */
	public static void enterDataAndClickOnGivenButton(Config testConfig, WebElement element, String value, String description, WebElement buttonToClick)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			String message = StringUtils.replaceEach(value, new String[] { "&", "\"", "<", ">" }, new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });
			testConfig.logComment("Enter the " + description + " as '" + message + "'");
			
			if(!enterDataInEditField(testConfig, element, value, buttonToClick))
			{
				try
				{
					click(testConfig, buttonToClick, "Hide keyboard button");
				}
				catch (org.openqa.selenium.WebDriverException e)
				{
					testConfig.logComment("Looks like keyboard is already hidden.Do Nothing");
				}
				testConfig.logComment("Unable to locate element. Scroll Down and try again");
				swipeBottomToTop(testConfig, 0.5);
				if(!enterDataInEditField(testConfig, element, value, buttonToClick))
				{
					try
					{
						click(testConfig, buttonToClick, "Hide keyboard button");
					}
					catch (org.openqa.selenium.WebDriverException e)
					{
						testConfig.logComment("Looks like keyboard is already hidden.Do Nothing");
					}
					testConfig.logComment("Unable to locate element. Scroll up and try again");
					swipeTopToBottom(testConfig, 0.5);
					enterDataInEditField(testConfig, element, value, buttonToClick);
				}
			}
		}
		else
		{
			testConfig.logComment("Skipped data entry for " + description);
		}
	}
	
	/**
	 * Move element to given location
	 * @param testConfig
	 * @param eX
	 * @param eY
	 * @param elementToMove
	 */
	public static void moveElementToGivenLocation(Config testConfig, int eX, int eY, WebElement elementToMove)
	{		
		int sX = elementToMove.getLocation().getX() + (elementToMove.getSize().width/2);
		int sY = elementToMove.getLocation().getY() + (elementToMove.getSize().height/2);
		
		if(eX == sX && eY == sY)
			testConfig.logComment("No need to move");
		else
		{
			testConfig.logComment("Moving from (" + eX + ", " + eY + ") to (" + sX + ", " + sY + ")");
			testConfig.appiumDriver.swipe(sX, sY, eX, eY, 500);
		}
	}
	
	/**
	 * Swipe using touch action
	 * @param testConfig
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public static  void swipeUsingTouchAction(Config testConfig, int startX, int startY, int endX, int endY)
	{
		try
		{
			TouchAction action = new TouchAction((MobileDriver<MobileElement>)testConfig.appiumDriver);
			action.longPress(startX, startY, 10).moveTo(endX, endY).release().perform();
		}
		catch(WebDriverException wde)
		{
			testConfig.logWarning("Some error occured in swipe");
		}
		testConfig.logComment("Swiped From (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
	}

	/**
 	 * This function is used to wait for an element only till a definite time.
 	 * @param testConfig
 	 * @param element
 	 * @param seconds
 	 */
 	public static void waitForElementvisibilitywithTime(Config testConfig,WebElement element,int seconds)
 	{
 			Boolean visible = false;
 			while(seconds>0)
 			{
 				try{
 					if(!element.isDisplayed())
 					{
 						seconds--;
 						testConfig.driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
 					}
 					else
 					{
 						visible=true;
 						seconds=0;
 						break;
 					}
 				}
 				catch(NoSuchElementException e)
 				{
 					seconds--;
					testConfig.driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
 				}
 			}
 			if(!visible)
 			{
 				testConfig.logFail("Could not find element");
 			}	
 	}
 	
 	/**
	 * Gets the WebElement using the specified locator technique on the passed
	 * driver page
	 * 
	 * @param Config
	 *            test config instance for the driver
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @return found WebElement
	 */
	public static MobileElement getPageElement(Config testConfig, How how, String what)
	{
		if(!(testConfig.getRunTimeProperty("disableGetPageElementLogs")!=null && testConfig.getRunTimeProperty("disableGetPageElementLogs").equalsIgnoreCase("true")))
		{
			testConfig.logComment("Get the WebElement with " + how + ":" + what);
		}
		
		what = Helper.replaceArgumentsWithRunTimeProperties(testConfig, what);
		
		try
		{
			switch (how)
			{
				case className:
					return testConfig.appiumDriver.findElement(By.className(what));
				case css:
					return testConfig.appiumDriver.findElement(By.cssSelector(what));
				case id:
					return testConfig.appiumDriver.findElement(By.id(what));
				case linkText:
					return testConfig.appiumDriver.findElement(By.linkText(what));
				case name:
					return testConfig.appiumDriver.findElement(By.name(what));
				case partialLinkText:
					return testConfig.appiumDriver.findElement(By.partialLinkText(what));
				case tagName:
					return testConfig.appiumDriver.findElement(By.tagName(what));
				case xPath:
					return testConfig.appiumDriver.findElement(By.xpath(what));
				case accessibility:
					return testConfig.appiumDriver.findElementByAccessibilityId(what);
				default:
					return null;
			}
		}
		catch (StaleElementReferenceException e1)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			// retry
			Browser.wait(testConfig, 3);
			testConfig.logComment("Retrying getting element" + how + ":" + what);
			return getPageElement(testConfig, how, what);
		}
		catch (NoSuchElementException e)
		{
			testConfig.logWarning("Could not find the element on page", true);
			return null;
		}
		
	}
	
	/**
	 * Select by value in radio group
	 * @param testConfig
	 * @param webElements - List of elements
	 * @param value - Value to select
	 * @param comment - Comments
	 */
	public static void selectByValueInRadioGroup(Config testConfig, WebElement webElement, String value, String comment)
	{
		testConfig.logComment("Selecting value '" + value + "' in radio group :" + comment);
		WebElement radioValue = null;
					
		try
		{
			
			radioValue = MobileActions.getPageElement(testConfig, How.xPath, "//*[@content-desc='" + value + "']");
			radioValue.click();
			testConfig.appiumDriver.getKeyboard().sendKeys(Keys.ENTER);
			
		}
		catch(Exception wde)
		{
			testConfig.logComment("Exception occurred in selecting value '" + value + "' for :" + comment + " : " + wde.getMessage());
		}
	}
}