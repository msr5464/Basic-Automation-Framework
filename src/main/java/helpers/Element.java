package helpers;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class Element 
{

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
}