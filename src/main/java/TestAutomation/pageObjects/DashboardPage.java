package TestAutomation.pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import TestAutomation.helpers.AssertHelper;
import TestAutomation.helpers.Config;
import TestAutomation.helpers.WaitHelper;

public class DashboardPage {

	@FindBy(css = "#user-links>li>details>summary>img")
	private WebElement userNameWidget;

	@FindBy(css = ".shelf-title")
	private WebElement mainHeading;

	public DashboardPage(Config testConfig) {
		PageFactory.initElements(testConfig.driver, this);
		WaitHelper.waitForPageLoad(testConfig, userNameWidget);
	}

	public void verifyDashboardPage(Config testConfig) {
		AssertHelper.compareEquals(testConfig, "Main Heading on Page", "Learn Git and GitHub without any code!", mainHeading.getText());
	}
}