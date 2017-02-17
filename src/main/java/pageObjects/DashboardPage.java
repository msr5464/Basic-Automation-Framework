package pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import helpers.Browser;
import helpers.Config;
import helpers.Helper;

public class DashboardPage 
{

	@FindBy(css=".header-nav-link.name.tooltipped.tooltipped-sw.js-menu-target")
	private WebElement userNameWidget;

	@FindBy(css=".shelf-title")
	private WebElement mainHeading;
	
	public DashboardPage(Config testConfig)
	{
		PageFactory.initElements(testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, userNameWidget);
	}
	
	public void verifyDashboardPage(Config testConfig)
	{
		Helper.compareEquals(testConfig, "Main Heading on Page", "Learn Git and GitHub without any code!", mainHeading.getText());
	}
}