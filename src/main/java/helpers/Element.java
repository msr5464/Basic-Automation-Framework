package helpers;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class Element 
{

	public static enum How
	{
		className, css, id, linkText, name, partialLinkText, tagName, xPath, accessibility
	};
	
	public static WebElement getPageElement(Config testConfig, How how, String what)
	{
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
			Browser.wait(testConfig, 3);
			testConfig.logComment("Retrying getting element" + how + ":" + what);
			return getPageElement(testConfig, how, what);
		}
		catch (NoSuchElementException e)
		{
			System.out.println("Could not find the element on page");
			return null;
		}
	}
	
	
	public static void click(Config testConfig, WebElement element, String description)
	{
		testConfig.logComment("Click on '" + description + "'");
		
		try
		{
			//Scroll Up or Down if element is not visible
			JavascriptExecutor jse = (JavascriptExecutor)testConfig.driver;
			jse.executeScript("arguments[0].scrollIntoView(false)", element);
		}
		catch(WebDriverException wde){}
		
		//Then click element
		element.click();
	}
	
	
	public static void enterData(Config testConfig, WebElement element, String value, String description)
	{
		testConfig.logComment("Enter the " + description + " as '" + value + "'");
		element.clear();
		element.sendKeys(value);
	}
	
	public static void KeyPress(Config testConfig, WebElement element, Keys key, String description)
	{
		testConfig.logComment("Press the key '" + key.toString() + "' on " + description + "");
		element.sendKeys(key);
		
	}
}