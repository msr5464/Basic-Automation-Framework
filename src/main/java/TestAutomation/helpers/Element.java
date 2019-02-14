package TestAutomation.helpers;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.support.ui.Select;

/**
 * This class contains all the functions which are needed to play with the Elements
 * @author MukeshR
 *
 */
public class Element 
{

	public static enum How
	{
		className, css, id, linkText, name, partialLinkText, tagName, xPath, accessibility
	};
	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to be clicked
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @param scroll           
	 *            used true whenever user want to scroll the element on the top of the page.
	 */
	public static void click(Config testConfig, WebElement elementToBeClicked, String description,boolean... scroll)
	{
		if(elementToBeClicked != null)
		{
			testConfig.logComment("Click on '" + description + "'");
			try
			{
				//Scroll Up or Down if element is not visible
				JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
				if(scroll.length>0&&scroll[0])
				{
					jse.executeScript("arguments[0].scrollIntoView(true)", elementToBeClicked);
				}
				else
				{
					jse.executeScript("arguments[0].scrollIntoView(false)", elementToBeClicked);
				}
			}
			catch(WebDriverException wde){}
			//Then click element
			elementToBeClicked.click();
		}
		else
		{
			if(testConfig.endExecutionOnfailure)
				testConfig.logFailToEndExecution("Element '"+description +"' not found on the page");
			else
				testConfig.logFail("Element '"+description +"' not found on the page");
		}
	}

	public static void enterData(Config testConfig, WebElement element, String value, String description)
	{
		testConfig.logComment("Enter the " + description + " as '" + value + "'");
		element.clear();
		element.sendKeys(value);
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
	public static void check(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Check '" + description + "'");
		if(element == null){
			testConfig.logFailToEndExecution("Element '" + description + "' is NULL, so can't check the checkbox");
		}
		else {
			try {
				if (!element.isSelected()){
					try {
						clickWithoutLog(testConfig, element);
						WaitHelper.waitForSeconds(testConfig, 1);
					}
					catch (StaleElementReferenceException e){
						testConfig.logWarning("Stale element reference exception. Trying again...");
						clickWithoutLog(testConfig, element);
					}
				}
				else {
					testConfig.logFail("Checkbox is already checked, so can't re-check it !!");
				}
			}
			catch(NoSuchElementException e){
				testConfig.logFailToEndExecution("Element '" + description + "' not found on page, so can't check the checkbox");
			}
		}
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
		if(element == null){
			testConfig.logFail("Element is NULL, so can't uncheck the checkbox!!");
		}
		else {
			if (element.isSelected()){
				try {
					clickWithoutLog(testConfig, element);
					WaitHelper.waitForSeconds(testConfig, 1);
				}
				catch (StaleElementReferenceException e){
					testConfig.logWarning("Stale element reference exception. Trying again...");
					clickWithoutLog(testConfig, element);
				}
			}
			else {
				testConfig.logFail("Checkbox is already unchecked, so can't uncheck it again !!");
			}
		}
	}


	/**
	 * Click without logging
	 * @param testConfig
	 * @param element
	 */
	private static void clickWithoutLog(Config testConfig, WebElement element)
	{
		try
		{
			JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
			jse.executeScript("arguments[0].scrollIntoView(false)", element);
			element.click();
		}
		catch(WebDriverException wde)
		{
			element.click();
		}
	}


	/**
	 * Gets the WebElement using the specified locator technique on the passed driver page
	 * 
	 * @param Config test config instance for the driver
	 * @param how Locator technique to use
	 * @param what element to be found with given technique (any arguments in this string will be replaced with run time properties)
	 * @return found WebElement
	 */
	public static WebElement getPageElement(Config testConfig, How how, String what)
	{
		testConfig.logComment("Get the WebElement with " + how + ":" + what);
		what = testConfig.replaceArgumentsWithRunTimeProperties(what);
		try
		{
			switch (how)
			{
			case className:
				return testConfig.driver.findElement(By.className(what));
			case css:
				return testConfig.driver.findElement(By.cssSelector(what));
			case id:
				return testConfig.driver.findElement(By.id(what));
			case linkText:
				return testConfig.driver.findElement(By.linkText(what));
			case name:
				return testConfig.driver.findElement(By.name(what));
			case partialLinkText:
				return testConfig.driver.findElement(By.partialLinkText(what));
			case tagName:
				return testConfig.driver.findElement(By.tagName(what));
			case xPath:
				return testConfig.driver.findElement(By.xpath(what));
			default:
				return null;
			}
		}
		catch (StaleElementReferenceException e1)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			// retry
			WaitHelper.waitForSeconds(testConfig, 2);
			testConfig.logComment("Retrying getting element" + how + ":" + what);
			return getPageElement(testConfig, how, what);
		}
		catch (NoSuchElementException e)
		{
			testConfig.logWarning("Could not find the element on page");
			return null;
		}
	}


	/**
	 * Gets the list of WebElements using the specified locator technique on the
	 * passed driver page
	 * 
	 * @param Config
	 *            test config instance for the driver
	 * @param how
	 *            Locator technique to use
	 * @param what
	 *            element to be found with given technique (any arguments in
	 *            this string will be replaced with run time properties)
	 * @return List of WebElements Found
	 */
	public static List<WebElement> getPageElements(Config testConfig, How how, String what)
	{
		testConfig.logComment("Get the List of WebElements with " + how + ":" + what);
		try
		{
			switch (how)
			{
			case className:
				return testConfig.driver.findElements(By.className(what));
			case css:
				return testConfig.driver.findElements(By.cssSelector(what));
			case id:
				return testConfig.driver.findElements(By.id(what));
			case linkText:
				return testConfig.driver.findElements(By.linkText(what));
			case name:
				return testConfig.driver.findElements(By.name(what));
			case partialLinkText:
				return testConfig.driver.findElements(By.partialLinkText(what));
			case tagName:
				return testConfig.driver.findElements(By.tagName(what));
			case xPath:
				return testConfig.driver.findElements(By.xpath(what));
			default:
				return null;
			}
		}
		catch (StaleElementReferenceException e1)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			// retry
			return getPageElements(testConfig, how, what);
		}
		catch (Exception e)
		{
			testConfig.logWarning("Could not find the list of the elements on page");
			return null;
		}
	}

	public WebElement getIframeElement(Config testConfig, How how, String what)
	{
		List<WebElement> frames = testConfig.driver.findElements(By.tagName("iframe"));

		if (frames.isEmpty())
			return null;
		WebElement element = null;

		for (WebElement fr : frames)
		{
			if (element != null)
			{
				return element;
			}

			try
			{
				testConfig.driver.switchTo().frame(fr);
			}
			catch (StaleElementReferenceException e)
			{
				testConfig.driver.switchTo().defaultContent();
				try
				{
					testConfig.driver.switchTo().frame(fr);
				}
				catch (StaleElementReferenceException ex)
				{
					testConfig.driver.switchTo().defaultContent();
				}
			}

			element = getPageElement(testConfig, how, what);

			if (element == null)
			{
				element = getIframeElement(testConfig, how, what);
			}
		}
		return element;
	}

	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param how
	 *            locator strategy to find element
	 * @param what
	 *            element locator
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 * @return
	 */
	public static String getText(Config testConfig, How how, String what, String description)
	{
		testConfig.logComment("Get text of '" + description + "'");
		String text = null;
		try
		{
			WebElement elm = Element.getPageElement(testConfig, how, what);
			text = Element.getText(testConfig, elm, description);
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			WebElement elm = Element.getPageElement(testConfig, how, what);
			text = Element.getText(testConfig, elm, description);

		}
		return text;
	}

	/**
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement whose text is needed
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static String getText(Config testConfig, WebElement element, String description)
	{
		String text = new String();
		try
		{
			try{
				//Scroll Up or Down if element is not visible
				JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
				jse.executeScript("arguments[0].scrollIntoView(false)", element);
			}
			catch(WebDriverException wde){
			}

			text = element.getText();
			if(text.equals(""))
			{
				text=element.getAttribute("value");
				if(text==null)
				{
					text="";
				}
			}
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");

			text = element.getText();
			if(text.equals(""))
			{
				text=element.getAttribute("value");
			}
		}
		catch (NoSuchElementException e)
		{
			testConfig.logWarning("Element '" + description + "' is not found on the page, so can't getText");
			text="";
		}
		return text;
	}
	
	public static String getSlectedValue(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Get text of '" + description + "'");
		String text = null;
		try
		{
			Select select = new Select(element);
			WebElement option = select.getFirstSelectedOption();
			text = option.getText();
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			Select select = new Select(element);
			WebElement option = select.getFirstSelectedOption();
			text = option.getText();
		}

		return text;
	}

	public static Boolean IsElementDisplayed(Config testConfig, WebElement element)
	{
		Boolean visible = true;
		if (element == null)
			return false;
		try
		{
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			visible = element.isDisplayed();
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			visible = element.isDisplayed();

		}
		catch (NoSuchElementException e)
		{
			visible = false;
		}
		catch (ElementNotVisibleException e)
		{
			visible = false;
		}

		finally
		{
			Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
			testConfig.driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		}
		return visible;
	}

	public static Boolean IsElementEnabled(Config testConfig, WebElement element)
	{
		Boolean visible = true;
		if (element == null)
			return false;
		try
		{
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			visible = element.isEnabled();
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			testConfig.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			visible = element.isDisplayed();

		}
		catch (NoSuchElementException e)
		{
			visible = false;
		}
		catch (ElementNotVisibleException e)
		{
			visible = false;
		}

		finally
		{
			Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
			testConfig.driver.manage().timeouts().implicitlyWait(ObjectWaitTime, TimeUnit.SECONDS);
		}
		return visible;
	}

	/**
	 * Presses the given Key in the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            Filename WebElement where data needs to be entered
	 * @param Key
	 *            key to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void KeyPress(Config testConfig, WebElement element, Keys key, String description)
	{
		testConfig.logComment("Press the key '" + key.toString() + "' on " + description + "");
		element.sendKeys(key);

	}

	/**
	 * @param Config
	 *            test config instance for the driver instance
	 * @param element
	 *            WebElement on which mouse is to be moved
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void mouseMove(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Move Mouse on '" + description + "'");

		Locatable hoverItem = (Locatable) element;
		Mouse mouse = ((HasInputDevices) testConfig.driver).getMouse();
		mouse.mouseMove(hoverItem.getCoordinates());

	}

	/**
	 * Method used to scroll up and down horizontally in browser
	 * 
	 * @param testConfig
	 * @param from
	 * @param to
	 */
	public static void pageScroll(Config testConfig, String from, String to)
	{
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript("window.scrollBy(" + from + "," + to + ")");
	}

	/**
	 * Makes changes in element's style to make it visible on page
	 * 
	 * @param testConfig
	 * @param csspath
	 *            - for locating element
	 * @param description
	 *            - for logging
	 * @return Webelement found
	 */
	public static WebElement reveal(Config testConfig, String csspath)
	{
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		String strJS = "document.querySelectorAll(\"" + csspath + "\")[0]";
		testConfig.logComment(strJS);
		js.executeScript(strJS + ".style.display = \"block\";");
		js.executeScript(strJS + ".style.visibility = 'visible';");
		js.executeScript(strJS + ".style.opacity = 1;");
		js.executeScript(strJS + ".style.width = '1px';");
		js.executeScript(strJS + ".style.height = '1px';");
		WebElement elementToBeClicked = getPageElement(testConfig, How.css, csspath);
		testConfig.logComment("Revealed element with css path " + csspath);
		return elementToBeClicked;
	}

	/**
	 * Hide Revealed Element
	 * @param testConfig
	 * @param csspath
	 */
	public static void hideRevealedElement(Config testConfig, String csspath)
	{
		try
		{
			JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
			String strJS = "document.querySelectorAll(\"" + csspath + "\")[0]";
			testConfig.logComment(strJS);
			js.executeScript(strJS + ".style.display = \"none\";");
			js.executeScript(strJS + ".style.visibility = 'hidden';");
			js.executeScript(strJS + ".style.opacity = 1;");
			js.executeScript(strJS + ".style.width = '1px';");
			js.executeScript(strJS + ".style.height = '1px';");
			testConfig.logComment("Revealed element with css path " + csspath + " is hidden now");
		}
		catch(Exception e)
		{
			testConfig.logWarning("Exception occured in hiding element.");
		}
	}

	/**
	 * Makes changes in element's style to make it visible on page
	 * 
	 * @param testConfig
	 * @param element
	 *            - Webelement to reveal
	 * @return Webelement
	 */
	public static WebElement reveal(Config testConfig, WebElement element)
	{
		JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
		js.executeScript("arguments[0].style.display = \"block\";", element);
		js.executeScript("arguments[0].style.visibility = 'visible';", element);
		js.executeScript("arguments[0].style.opacity = 1;", element);
		js.executeScript("arguments[0].style.width = '1px';", element);
		js.executeScript("arguments[0].style.height = '1px';", element);
		return element;
	}

	/**
	 * This function reveals and clicks on element
	 * 
	 * @param testConfig
	 *            - for logging purposes
	 * @param csspath
	 *            - path to element
	 * @param description
	 *            - for logging purposes
	 */
	public static void revealAndClick(Config testConfig, String csspath, String description)
	{
		WebElement elementToBeClicked = reveal(testConfig, csspath);
		try
		{
			Element.click(testConfig, elementToBeClicked, description);
		}
		catch (StaleElementReferenceException elementReferenceException)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			elementToBeClicked = getPageElement(testConfig, How.css, csspath);
			Element.click(testConfig, elementToBeClicked, description);

		}
		hideRevealedElement(testConfig, csspath);
	}

	/**
	 * This function reveals file input element and sends file path
	 * 
	 * @param testConfig
	 *            - for logging purposes
	 * @param csspath
	 *            - path to uploader input
	 * @param filePath
	 *            - path to file
	 * @param description
	 *            - for logging purposes
	 */
	public static void revealInputAndUploadFile(Config testConfig, String csspath, String filePath, String description)
	{
		if(!File.separator.equals("\\"))
			filePath = filePath.replaceAll("\\\\", File.separator);

		WebElement fileInput = reveal(testConfig, csspath);
		try
		{
			Element.enterFileName(testConfig, fileInput, filePath, description);
		}
		catch (StaleElementReferenceException elementReferenceException)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			fileInput = getPageElement(testConfig, How.css, csspath);
			Element.enterFileName(testConfig, fileInput, filePath, description);
		}

		try
		{
			fileInput = getPageElement(testConfig, How.css, csspath);
		}
		catch(UnhandledAlertException uae)
		{}

		if(!Popup.isAlertPresent(testConfig) && fileInput.isDisplayed())
		{
			Element.clickThroughJS(testConfig, fileInput, "File input");
			WaitHelper.waitForSeconds(testConfig, 3);

			Element.enterFileName(testConfig, fileInput, filePath, description);

			WaitHelper.waitForSeconds(testConfig, 2);
		}
	}

	/**
	 * Selects the given 'value' attribute for the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to select
	 * @param value
	 *            value to the selected
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void selectDropdownValue(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			testConfig.logComment("Select the " + description + " dropdown value '" + value + "'");

			Select sel = new Select(element);
			sel.selectByValue(value);
		}
		else
		{
			testConfig.logComment("Skipped value selection for " + description);
		}
	}
	public static List<WebElement> getAllDropdownOptions(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Get all options of " + description + ".");
		Select sel = new Select(element);
		return sel.getOptions();
	}
	public static WebElement getSelectedDropdownOption(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Get first selected option of " + description + ".");
		Select sel = new Select(element);
		return sel.getFirstSelectedOption();
	}
	public static void dragAndDropElement(Config testConfig, WebElement sourceElement,WebElement targetElement, String sourceDescription,String targetDescription)
	{
		testConfig.logComment("Dragging element " + sourceDescription + " to "+targetDescription);
		Actions actions=new Actions(testConfig.driver);
		actions.dragAndDrop(sourceElement, targetElement).perform();
	}
	/**
	 * Selects the given visible text 'value' for the specified WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            WebElement to select
	 * @param value
	 *            visible text value to the selected
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void selectVisibleText(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{
			testConfig.logComment("Select the " + description + " dropdown text '" + value + "'");

			Select sel = new Select(element);
			sel.selectByVisibleText(value);

			try
			{
				sel = new Select(element);
				element.click();
				sel.selectByVisibleText(value);
			}
			catch(Exception e){}
		}
		else
		{
			testConfig.logComment("Skipped text selection for " + description);
		}
	}

	/**
	 * @param testConfig  
	 * 				Config instance used for logging
	 * @param element
	 * 				 WebElement to select
	 * @param value 
	 * 				String of Visible text values to be selected
	 * @param description 
	 * 				 for logging purpose.
	 */
	public static void selectVisibleText(Config testConfig, WebElement element, String [] value, String description)
	{
		Select sel = new Select(element);
		sel.deselectAll();
		for(int i=0;i<value.length;i++)
		{
			if (!value[i].equalsIgnoreCase("{skip}"))
			{
				testConfig.logComment("Select the " + description + " dropdown text '" + value[i] + "'");
				sel.selectByVisibleText(value[i]);
			}
			else
			{
				testConfig.logComment("Skipped text selection for " + description);
			}
		}
	}


	/**
	 * Enters the given 'value'in the specified File name WebElement
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            Filename WebElement where data needs to be entered
	 * @param value
	 *            value to the entered
	 * @param description
	 *            logical name of specified WebElement, used for Logging
	 *            purposes in report
	 */
	public static void enterFileName(Config testConfig, WebElement element, String value, String description)
	{
		if (!value.equalsIgnoreCase("{skip}"))
		{

			testConfig.logComment("Enter the " + description + " as '" + value + "'");
			element.sendKeys(value);

		}
		else
		{
			testConfig.logComment("Skipped file entry for " + description);
		}
	}


	/**
	 * Clicks on element using JavaScript
	 * 
	 * @param testConfig
	 *            For logging
	 * @param elementToBeClicked
	 *            - Element to be clicked
	 * @param description
	 *            For logging
	 */
	public static void clickThroughJS(Config testConfig, WebElement elementToBeClicked, String description)
	{
		try {
			JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
			js.executeScript("arguments[0].click();", elementToBeClicked);
			testConfig.logComment("Clicked on " + description);
		}
		catch(Exception e){
			testConfig.logFail("Unable to clickThroughJS on element '"+description+"'");
		}
	}


	public static void clickAndWaitForElementToHide(Config testConfig, WebElement elementToBeClicked, String description)
	{
		click(testConfig, elementToBeClicked, description);
		WaitHelper.waitForElementToBeHidden(testConfig, elementToBeClicked, description);
	}

	/**
	 * Warning : Don't use this function for regular cases its just for special case when click is not working in 1 time
	 * This function will keep clicking on the 'elementToBeClicked' till the time 'nextElement' is load displayed.
	 * @param testConfig
	 * @param elementToBeClicked
	 * @param nextElement
	 */
	public static void clickUntilNextElementIsLoaded(Config testConfig, WebElement elementToBeClicked, String description, WebElement nextElement)
	{
		testConfig.logComment("Executing clickUntilNextElementIsLoaded for : "+description);
		WaitHelper.waitForElementToBeClickable(testConfig, elementToBeClicked, description);
		click(testConfig, elementToBeClicked, "elementToBeClicked and wait for nextElement");
		WaitHelper.waitForSeconds(testConfig, 3);
		int counter = 5;
		while(counter>0 && !Element.IsElementDisplayed(testConfig, nextElement))
		{
			JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
			js.executeScript("arguments[0].click();", elementToBeClicked);
			WaitHelper.waitForSeconds(testConfig, 3);

			if(Element.IsElementDisplayed(testConfig, nextElement))
				break;

			elementToBeClicked.click();
			WaitHelper.waitForSeconds(testConfig, 2);
			counter--;
		}
	}

	/**
	 * Warning : Don't use this function for regular cases its just for special case when click is not working in 1 time
	 * This function will keep clicking on the 'elementToBeClicked' till the time 'elementToHide' is becomes invisible from the page.
	 * @param testConfig
	 * @param elementToBeClicked
	 */
	public static void clickUntilNextElementIsHidden(Config testConfig, WebElement elementToBeClicked, String description, WebElement elementToHide)
	{
		testConfig.logComment("Executing clickUntilNextElementIsHidden for : "+description);
		click(testConfig, elementToBeClicked, description);
		WaitHelper.waitForSeconds(testConfig, 3);
		int counter = 5;
		while(counter>0 && Element.IsElementDisplayed(testConfig, elementToHide))
		{
			JavascriptExecutor js = (JavascriptExecutor) testConfig.driver;
			js.executeScript("arguments[0].click();", elementToBeClicked);
			WaitHelper.waitForSeconds(testConfig, 3);

			if(!Element.IsElementDisplayed(testConfig, elementToHide))
				break;

			elementToBeClicked.click();
			WaitHelper.waitForSeconds(testConfig, 2);
			counter--;
		}
	}

	/**
	 * Verifies if element is present on the page
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            element to be verified
	 * @param description
	 *            description logical name of specified WebElement, used for
	 *            Logging purposes in report
	 */
	public static void verifyElementPresent(Config testConfig, WebElement element, String description)
	{
		//Scroll Up or Down if element is not visible
		try
		{
			JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
			jse.executeScript("arguments[0].scrollIntoView(false)", element);
		}catch(WebDriverException wde){}
		if (element != null && element.isDisplayed())
		{
			testConfig.logPass("Verified the presence of element '" + description + "' on the page");
		}
		else
		{
			testConfig.logFail("Element '" + description + "' is not present on the page");
		}

	}


	/**
	 * Verifies if element is absent on the page
	 * 
	 * @param testConfig
	 *            Config instance used for logging
	 * @param element
	 *            element to be verified
	 * @param description
	 *            description logical name of specified WebElement, used for
	 *            Logging purposes in report
	 */
	public static void verifyElementNotPresent(Config testConfig, WebElement element, String description)
	{
		try
		{
			if (!IsElementDisplayed(testConfig, element))
			{
				testConfig.logPass("Verified the absence of element '" + description + "' on the page");
			}

			else
			{
				testConfig.logFail("Element '" + description + "' is present on the page");
			}
		}
		catch (StaleElementReferenceException e)
		{
			testConfig.logComment("Stale element reference exception. Trying again...");
			if (!IsElementDisplayed(testConfig, element))
			{
				testConfig.logPass("Verified the absence of element '" + description + "' on the page");
			}

			else
			{
				testConfig.logFail("Element '" + description + "' is present on the page");
			}
		}
	}
}