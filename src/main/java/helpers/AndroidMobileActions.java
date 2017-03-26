package helpers;


import java.text.DateFormatSymbols;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidKeyCode;

public class AndroidMobileActions extends MobileActions
{
	/**
	 * Scroll down to element with given text
	 * @param testConfig
	 * @param elementText
	 */
	public static void scrollToElementWithGivenText(Config testConfig, String elementText)
	{
		boolean elementFound = scrollToElementWithGivenText(testConfig, elementText, "down");
		if(!elementFound)
			scrollToElementWithGivenText(testConfig, elementText, "up");
	}
	
	/**
	 * Scroll to given direction to find element with given text
	 * @param testConfig
	 * @param elementText
	 * @param scrollDirection - up/down
	 */
	public static boolean scrollToElementWithGivenText(Config testConfig, String elementText, String scrollDirection)
	{
		return scrollToElementWithGivenText(testConfig, How.className, "android.widget.ScrollView", elementText, scrollDirection);
	}

	/**
	 * Scroll to given direction to find element with given text
	 * @param testConfig
	 * @param how
	 * @param property
	 * @param elementText
	 * @param scrollDirection - up/down
	 */
	public static boolean scrollToElementWithGivenText(Config testConfig, How how, String property, String elementText, String scrollDirection)
	{
		WebElement element = getPageElement(testConfig, how, property);
		boolean elementFound = false;
		if(element != null)
		{
			List<WebElement> elements = getListOfElements(testConfig, How.xPath, "//*[@text='" + elementText + "']");
			int count = 1;
			Object[] lastElement1Details = getElementDetails(getLastElementInsideAnElement(testConfig, how, property));
			Object[] lastElement2Details = null;
			boolean lastElementMatched = false;
			while(elements.size() == 0)
			{	
				if(scrollDirection.equals("down"))
					swipeBottomToTopWithinGivenObject(testConfig, element);
				else
					swipeTopToBottomWithinGivenObject(testConfig, element);

				elements = getListOfElements(testConfig, How.xPath, "//*[@text='" + elementText + "']");
				if(elements.size() == 0)
				{
					lastElement2Details = getElementDetails(AndroidMobileActions.getLastElementInsideAnElement(testConfig, how, property));
					if(lastElement1Details != null && lastElement2Details != null)
					{
						lastElementMatched = compareTwoElements(lastElement1Details, lastElement2Details);
						lastElement1Details = lastElement2Details;
					}
					else
						lastElement1Details = lastElement2Details;
				}

				if (count >= 5 || lastElementMatched)
					break;

				count++;
			}

			if(elements.size() != 0)
			{
				bringObjectToTop(testConfig, element, elements.get(0));
				elementFound = true;
			}
		}
		else
			testConfig.logComment("Element could not be located");
		Browser.wait(testConfig, 2);
		return elementFound;
	}
	
	/**
	 * Bring element2 to the top inside element1
	 * @param testConfig
	 * @param element1
	 * @param element2
	 */
	public static void bringObjectToTop(Config testConfig, WebElement element1, WebElement element2)
	{
		if(element1 == null || element2 == null)
			testConfig.logComment("Element could not be located");
		else
		{
			int eX = element1.getLocation().getX() + (element1.getSize().width/2);
			int eY = element1.getLocation().getY() + (element2.getSize().getHeight()/2);
			
			int sX = element2.getLocation().getX() + (element2.getSize().width/2);
			int sY = element2.getLocation().getY() + (element2.getSize().getHeight()/2);

			if(sY > eY+20)
				moveElement(testConfig, element2, eX, eY+100);
			
			testConfig.logComment("Element moved from (" + sX + "," + sY + ") to (" + eX + "," + eY + ") position");
		}
	}
	
	/**
	 * Move element to specified position
	 * @param testConfig
	 * @param elementToMove
	 * @param x
	 * @param y
	 */
	public static void moveElement(Config testConfig, WebElement elementToMove, int x, int y)
	{
		TouchAction action = new TouchAction((MobileDriver<MobileElement>)testConfig.appiumDriver);
		action.longPress(elementToMove).moveTo(x, y).release().perform();
	}
	
	/**
	 * Compare properties of two elements for uniqueness
	 * @param element1
	 * @param element2
	 * @return true/false
	 */
	public static boolean compareTwoElements(Object[] element1, Object[] element2)
	{
		if(!((String)element1[0]).equalsIgnoreCase((String)element2[0]))
			return false;

		if(!((String)element1[1]).equalsIgnoreCase((String)element2[1]))
			return false;
		
		if(!((String)element1[2]).equalsIgnoreCase((String)element2[2]))
			return false;

		Dimension element1Size = (Dimension)element1[3];
		Dimension element2Size = (Dimension)element2[3];
		if(!(element1Size.height == element2Size.height) || !(element1Size.width == element2Size.width))
			return false;

		return true;
	}
	
	public static Object[] getElementDetails(WebElement element)
	{
		String index1;
		Object detailsArray[] = new Object[4];
		try
		{
			try
			{
				index1 = element.getAttribute("index");
			}
			catch(Exception e)
			{
				index1 = "No Index";
			}

			detailsArray[0] = index1;
			detailsArray[1] = element.getText();
			detailsArray[2] = element.getTagName();
			detailsArray[3] = element.getSize();
		}
		catch(Exception e)
		{
			return null;
		}
		
		return detailsArray;
	}

	/**
	 * Get last element inside an element
	 * @param testConfig
	 * @param how
	 * @param property
	 * @return WebElement
	 */
	public static WebElement getLastElementInsideAnElement(Config testConfig, How how, String property)
	{
		String newProperty = null;
		switch(how)
		{
			case id:
				newProperty = "//*[@resource-id='" + property + "']//*";
				break;
			case className:
				newProperty = "//*[@class='" + property + "']//*";
				break;
			case xPath:
				newProperty = property + "//*";
				break;
			default:
				testConfig.logWarning("How type is not supported");
		}
		
		return AndroidMobileActions.getLastElementInCollection(testConfig, How.xPath, newProperty);
	}
	
	/**
	 * Swipe Bottom To Top Within Given Object
	 * @param testConfig
	 * @param element
	 */
	public static void swipeBottomToTopWithinGivenObject(Config testConfig, WebElement element)
	{
		if(element == null)
			testConfig.logComment("Element could not be located");
		else
		{
			int sX = element.getLocation().getX() + (element.getSize().width/2);
			int sY = element.getLocation().getY() + element.getSize().getHeight()-15;
			int eY = element.getLocation().getY();

			swipeUsingTouchAction(testConfig, sX, sY, sX, eY);
		}
	}
	
	/**
	 * Swipe Top To Bottom Within Given Object
	 * @param testConfig
	 * @param element
	 */
	public static void swipeTopToBottomWithinGivenObject(Config testConfig, WebElement element)
	{
		if(element == null)
			testConfig.logComment("Element could not be located");
		else
		{
			int sX = element.getLocation().getX() + (element.getSize().width/2);
			int sY = element.getLocation().getY() + element.getSize().getHeight()/5;
			int eY = element.getLocation().getY() + element.getSize().getHeight()/2;

			swipeUsingTouchAction(testConfig, sX, sY, sX, eY);
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
		WebElement listOption = null;
		testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		
		try
		{
			listOption = testConfig.driver.findElement(By.xpath("//android.widget.ListView//*[@text='" + optionText + "']"));
		}
		catch(Exception ex)
		{
			testConfig.logComment("Option - \"" + optionText + "\" is not found");
		}
		
		try
		{
			if(listOption == null)
			{
				scrollToElementWithGivenText(testConfig, How.className, "android.widget.ListView", optionText, "up");
				listOption = testConfig.driver.findElement(By.xpath("//android.widget.ListView//*[@text='" + optionText + "']"));
			}
		}
		catch(Exception ex)
		{
			testConfig.logComment("Option - \"" + optionText + "\" is still not found");
		}
		
		try
		{
			if(listOption == null)
			{
				scrollToElementWithGivenText(testConfig, How.className, "android.widget.ListView", optionText, "down");
				listOption = testConfig.driver.findElement(By.xpath("//android.widget.ListView//*[@text='" + optionText + "']"));
			}
			click(testConfig, listOption, optionText);
		}
		catch(Exception ex)
		{
			testConfig.logFail("Option - \"" + optionText + "\" is still not found");
			goBack(testConfig);
		}
		
		finally
		{
			Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
			testConfig.driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		}
		
		/*HashMap<String, String> scrollObject = new HashMap<String, String>();
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		
		RemoteWebElement listElement = (RemoteWebElement) testConfig.driver.findElement(By.className("android.widget.ListView"));
		String listId = ((RemoteWebElement) listElement).getId();
		scrollObject.put("text", optionText);
		scrollObject.put("element", listId);
		scrollObject.put("direction", "up");
		WebElement listOption = null;
		try
		{
			listOption = testConfig.driver.findElement(By.xpath("//android.widget.ListView//*[@text='" + optionText + "']"));
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
				
				listOption = testConfig.driver.findElement(By.xpath("//android.widget.ListView//*[@text='" + optionText + "']"));
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
				
				listOption = testConfig.driver.findElement(By.xpath("//android.widget.ListView//*[@text='" + optionText + "']"));
			}
			click(testConfig, listOption, optionText);
		}
		catch(Exception ex)
		{
			testConfig.logFail("Option - \"" + optionText + "\" is still not found");
			goBack(testConfig);
		}*/
	}
	
	/**
	 * Menu  Option in Mobile
	 * 
	 * @param testConfig
	 */
	public static void clickMobileMenu(Config testConfig,String description)
	{
		testConfig.logComment("Click " + description + "'");
		testConfig.androidDriver.pressKeyCode(AndroidKeyCode.MENU);
	}
	
	/**
	 * Click on droplist and select an option
	 * @param testConfig
	 * @param dropDownObject
	 * @param optionText
	 * @param description
	 */
	public static void clickOnListAndSelectOption(Config testConfig, WebElement dropDownObject, String optionText, String description)
	{
		click(testConfig, dropDownObject, description);
		selectFromListView(testConfig, optionText);
	}
	
	/**
	 * Select date in calendar
	 * @param testConfig
	 * @param xpathForYearPicker
	 * @param xpathForMonthPicker
	 * @param xpathForDayPicker
	 * @param xPathForSetButton
	 * @param date
	 */
	public static void selectDateInCalendar(Config testConfig, String xpathForYearPicker, String xpathForMonthPicker, String xpathForDayPicker, String xPathForSetButton, String date)
	{
		testConfig.logComment("Selecting date '" + date + "' in calendar");
		
		//Get element for full calendar
		List<WebElement> yearList = getListOfElements(testConfig, How.id, "android:id/date_picker_header_year");
		if(yearList == null || yearList.size() == 0)
		{
			DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
			String dateMonthYearTemp[] = date.split("-");
			String dateMonthYear[] = new String[3];
			if(dateMonthYearTemp.length == 2)
			{
				dateMonthYear[0] = "NA";
				dateMonthYear[1] = dateMonthYearTemp[0];
				dateMonthYear[2] = dateMonthYearTemp[1];
			}
			else
			{
				dateMonthYear = dateMonthYearTemp;
			}

			int expiryMonth = Integer.valueOf(dateMonthYear[1]);

			WebElement yearElement = testConfig.appiumDriver.findElementByXPath(xpathForYearPicker + "/android.widget.EditText");
			WebElement yearElementDown = null;
			WebElement yearElementUp = null;
			int difference = Integer.parseInt(yearElement.getText()) - Integer.parseInt(dateMonthYear[2]);

			String selectedYear = "";
			int maxCounter = 30;
			while(difference > 0 && !selectedYear.equalsIgnoreCase(dateMonthYear[2]) && maxCounter > 0)
			{
				yearElementUp = testConfig.appiumDriver.findElementByXPath(xpathForYearPicker + "/android.widget.Button[1]");
				AndroidMobileActions.tap(testConfig, yearElementUp, "Year up");
				Browser.wait(testConfig, 1);
				selectedYear = testConfig.appiumDriver.findElementByXPath(xpathForYearPicker + "/android.widget.EditText").getText();
				maxCounter--;
			}
			maxCounter = 30;
			while(difference < 0 && !selectedYear.equalsIgnoreCase(dateMonthYear[2]) && maxCounter > 0)
			{
				yearElementDown = testConfig.appiumDriver.findElementByXPath(xpathForYearPicker + "/android.widget.Button[last()]");
				AndroidMobileActions.tap(testConfig, yearElementDown, "Year down");
				Browser.wait(testConfig, 1);
				selectedYear = testConfig.appiumDriver.findElementByXPath(xpathForYearPicker + "/android.widget.EditText").getText();
				maxCounter--;
			}
			difference = 0;
			String selectedMonth = testConfig.appiumDriver.findElementByXPath(xpathForMonthPicker + "/android.widget.EditText").getText();
			String[] months = dateFormatSymbols.getMonths();
			String monthNameToSelect = "";
			int selectedMonthLength = selectedMonth.trim().length();
			if(selectedMonthLength == 1 || selectedMonthLength == 2)
			{
				difference = Integer.parseInt(selectedMonth) - expiryMonth;
				monthNameToSelect = dateMonthYear[1];
			}
			else if (selectedMonthLength == 3)
			{
				monthNameToSelect = months[expiryMonth-1].subSequence(0, 3).toString();
				for(int i = 0; i < months.length; i++)
				{
					if(months[i].subSequence(0, 3).equals(selectedMonth))
					{
						difference = i + 1 - expiryMonth;
						break;
					}
				}
			}
			WebElement monthElementDown = null;
			WebElement monthElementUp = null;
			maxCounter = 12;
			
			while(difference > 0 && !selectedMonth.equalsIgnoreCase(monthNameToSelect) && maxCounter > 0)
			{
				monthElementUp = testConfig.appiumDriver.findElementByXPath(xpathForMonthPicker + "/android.widget.Button[1]");
				testConfig.logComment("Tap month value : " + monthElementUp.getText());
				AndroidMobileActions.tap(testConfig, monthElementUp, "Month up");
				Browser.wait(testConfig, 1);
				selectedMonth = testConfig.appiumDriver.findElementByXPath(xpathForMonthPicker + "/android.widget.EditText").getText();
				maxCounter--;
			}
			maxCounter = 12;
			while(difference < 0 && !selectedMonth.equalsIgnoreCase(monthNameToSelect) && maxCounter > 0)
			{
				monthElementDown = testConfig.appiumDriver.findElementByXPath(xpathForMonthPicker + "/android.widget.Button[last()]");
				testConfig.logComment("Tap month value : " + monthElementDown.getText());
				AndroidMobileActions.tap(testConfig, monthElementDown, "Month down");
				Browser.wait(testConfig, 1);
				selectedMonth = testConfig.appiumDriver.findElementByXPath(xpathForMonthPicker + "/android.widget.EditText").getText();
				maxCounter--;
			}
			if(!dateMonthYear[0].equalsIgnoreCase("NA"))
			{
				difference = 0;
				WebElement dateElement = testConfig.appiumDriver.findElementByXPath(xpathForDayPicker + "/android.widget.EditText");
				WebElement dateElementDown = null;
				WebElement dateElementUp = null;
				difference = Integer.parseInt(dateElement.getText()) - Integer.parseInt(dateMonthYear[0]);
				/*if(Math.abs(difference)>15)
				if(difference<0)
					difference = difference + 30;
				else
					difference = difference - 30;*/
				String selectedDate = "";
				maxCounter = 30;
				while(difference > 0 && !selectedDate.equalsIgnoreCase(dateMonthYear[0]) && maxCounter > 0)
				{
					dateElementUp = testConfig.appiumDriver.findElementByXPath(xpathForDayPicker + "/android.widget.Button[1]");
					testConfig.logComment("Select day value : " + dateElementUp.getText());
					AndroidMobileActions.tap(testConfig, dateElementUp, "Date up");
					Browser.wait(testConfig, 1);
					selectedDate = testConfig.appiumDriver.findElementByXPath(xpathForDayPicker + "/android.widget.EditText").getText();
					maxCounter--;
				}
				maxCounter = 30;
				while(difference < 0 && !selectedDate.equalsIgnoreCase(dateMonthYear[0]) && maxCounter > 0)
				{
					dateElementDown = testConfig.appiumDriver.findElementByXPath(xpathForDayPicker + "/android.widget.Button[last()]");
					testConfig.logComment("Select day value : " + dateElementDown.getText());
					AndroidMobileActions.tap(testConfig, dateElementDown, "Date down");
					Browser.wait(testConfig, 1);
					selectedDate = testConfig.appiumDriver.findElementByXPath(xpathForDayPicker + "/android.widget.EditText").getText();
					maxCounter--;
				}
			}
			AndroidMobileActions.click(testConfig, testConfig.appiumDriver.findElement(By.xpath(xPathForSetButton)), "Set/Done Button");
		}
		else
		{
			selectDateInFullCalendar(testConfig, date);
		}
	}
	
	/**
	 * Get Current Activity for android app
	 * @param testConfig
	 * @return activity name
	 */
	public static String getAndroidAppCurrentActivity(Config testConfig)
	{
		if(testConfig.androidDriver != null)
			return testConfig.androidDriver.currentActivity();
		else
			return null;
	}
	
	/**
	 * Allow Or Deny All Permissions 
	 * @param testConfig
	 * @param buttonToClick
	 * @param comment
	 */
	public static void allowOrDenyAllPermissionsOnAndroidApp(Config testConfig, WebElement buttonToClick, String comment)
	{
		String currentActivity = AndroidMobileActions.getAndroidAppCurrentActivity(testConfig);
		if(currentActivity != null && currentActivity.toLowerCase().contains("grantpermissionsactivity"))
		{
			do
			{
				AndroidMobileActions.click(testConfig, buttonToClick, comment);
				Browser.wait(testConfig, 1);
				currentActivity = AndroidMobileActions.getAndroidAppCurrentActivity(testConfig);
			}
			while(currentActivity != null && currentActivity.toLowerCase().contains("grantpermissionsactivity"));
		}
		else
			testConfig.logWarning("Permission dialog not found. Current Activity was : " + currentActivity);
	}
	
	/**
	 * Select date in full calendar
	 * @param testConfig
	 * @param date
	 */
	public static void selectDateInFullCalendar(Config testConfig, String date)
	{
		testConfig.logComment("Selecting date '" + date + "' in calendar");
		
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
		String dateMonthYearTemp[] = date.split("-");
		String dateMonthYear[] = new String[3];
		if(dateMonthYearTemp.length == 2)
		{
			dateMonthYear[0] = "NA";
			dateMonthYear[1] = dateMonthYearTemp[0];
			dateMonthYear[2] = dateMonthYearTemp[1];
		}
		else
		{
			dateMonthYear = dateMonthYearTemp;
		}
		
		WebElement yearObject = getPageElement(testConfig, How.id, "android:id/date_picker_header_year");
		int expiryMonth = Integer.valueOf(dateMonthYear[1]);
		AndroidMobileActions.clickOnListAndSelectOption(testConfig, yearObject, dateMonthYear[2], "Select year");
		Browser.wait(testConfig, 1);
		
		int difference = 0;
		WebElement firstDateElement = getPageElement(testConfig, How.xPath, "//android.view.View[@resource-id='android:id/month_view']/android.view.View[@text='1']");
        String selectedMonth = firstDateElement.getAttribute("name").split(" ")[1];
        
        if(selectedMonth.trim().length() == 2)
        	difference = Integer.parseInt(selectedMonth) - expiryMonth;
        else
        {
        	String[] months = dateFormatSymbols.getMonths();
        	for(int i = 0; i < months.length; i++)
        	{
        		if(months[i].equals(selectedMonth))
        		{
        			difference = i + 1 - expiryMonth;
        			break;
        		}
        	}
        }
		
        WebElement previousMonth = getPageElement(testConfig, How.id, "android:id/prev");
		while(difference > 0)
		{
			MobileActions.click(testConfig, previousMonth, "Previous month");
			difference--;
			Browser.wait(testConfig, 1);
		}
		
		WebElement nextMonth = getPageElement(testConfig, How.id, "android:id/next");
		while(difference < 0)
		{
			MobileActions.click(testConfig, nextMonth, "Next month");
			difference++;
			Browser.wait(testConfig, 1);
		}
		
		if(!dateMonthYear[0].equalsIgnoreCase("NA"))
		{
			WebElement dateElement = testConfig.appiumDriver.findElementByXPath("//*[@resource-id='android:id/month_view']/android.view.View[@text='" + Integer.parseInt(dateMonthYear[0]) + "']");
			MobileActions.tap(testConfig, dateElement, "Select Date");
		}
						
		WebElement calendarOk = getPageElement(testConfig, How.id, "android:id/button1");
		MobileActions.click(testConfig, calendarOk, "Ok Button");
	}
	
	/**
	 * Press keycode
	 * @param testConfig
	 * @param keyCode
	 * @param description
	 */
	public static void pressKeyCode(Config testConfig, int keyCode, String description)
	{
		testConfig.logComment("Press key '" + description + "'");
		testConfig.androidDriver.pressKeyCode(keyCode);
	}
	
	/**
	 * Start app with given activity
	 * @param testConfig
	 * @param appPackage - App package name
	 * @param appActivity - app activity name
	 */
	public static void startAppWithGivenActivity(Config testConfig, String appPackage, String appActivity)
	{
		testConfig.logComment("Starting app \"" + appPackage + "\" with activity \"" + appActivity + "\"");
		try
		{
			testConfig.androidDriver.startActivity(appPackage, appActivity);
			testConfig.logComment("App \"" + appPackage + "\" with activity \"" + appActivity + "\" has been launched successfully");
		}
		catch(WebDriverException wde)
		{
			String exceptionMessage = wde.getMessage();
			if(exceptionMessage != null && exceptionMessage.contains("Activity used to start app doesn't exist"))
			{
				testConfig.logWarning("App \"" + appPackage + "\" with activity \"" + appActivity + "\" could not be started as app is not installed or activity is not launchable");
			}
			else
			{
				testConfig.logWarning("App \"" + appPackage + "\" with activity \"" + appActivity + "\" could not be started. Exception is : " + exceptionMessage);
			}
		}
		catch(Exception e)
		{
			testConfig.logWarning("App \"" + appPackage + "\" with activity \"" + appActivity + "\" could not be started. Exception is : " + e.getMessage());
		}
	}
	
	/**
	 * Turn Wifi On
	 * @param testConfig
	 */
	public static void turnWifiOn(Config testConfig)
	{
		testConfig.logComment("Turning on wifi");
		AndroidWifiUtility wifiUtil = new AndroidWifiUtility(testConfig);
		wifiUtil.turnWifiOn();
	}
	
	/**
	 * Turn Wifi Off
	 * @param testConfig
	 */
	public static void turnWifiOff(Config testConfig)
	{
		testConfig.logComment("Turning off wifi");
		AndroidWifiUtility wifiUtil = new AndroidWifiUtility(testConfig);
		wifiUtil.turnWifiOff();
	}
	
	/**
	 * Open notifications
	 * @param testConfig
	 */
	public static void openNotification(Config testConfig)
	{
		try
		{
			testConfig.logComment("Opening notification");
			testConfig.androidDriver.openNotifications();
			Browser.wait(testConfig, 2);
		}
		catch(Exception e)
		{
			testConfig.logFail("Notification could not open: " + e.getMessage());
		}
	}
	
	/*
	 * Close app associated with the driver
	 */
	public static void closeApp(Config testConfig)
	{
		try
		{
			testConfig.logComment("Closing App");
			testConfig.androidDriver.closeApp();
		}
		catch(Exception e)
		{
			testConfig.logFail("App could not be closed: " + e.getMessage());
		}
	}
	
	/*
	 * Open app associated with the driver
	 */
	public static void openApp(Config testConfig)
	{
		try
		{
			testConfig.logComment("Launching App");
			testConfig.androidDriver.launchApp();
		}
		catch(Exception e)
		{
			testConfig.logFail("App could not be launched: " + e.getMessage());
		}
	}
	
	/**
	 * Home button in Mobile
	 * 
	 * @param testConfig
	 */
	public static void clickHomeButton(Config testConfig)
	{
		testConfig.logComment("Click Home button");
		testConfig.androidDriver.pressKeyCode(AndroidKeyCode.HOME);
	}
	
	/**
	 * Open Recent Apps
	 * 
	 * @param testConfig
	 */
	public static void openRecentApps(Config testConfig)
	{
		testConfig.logComment("Click recent app drawer");
		testConfig.androidDriver.pressKeyCode(AndroidKeyCode.KEYCODE_APP_SWITCH);
	}
}
