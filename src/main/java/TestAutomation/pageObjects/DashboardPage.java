package TestAutomation.pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import TestAutomation.helpers.AssertHelper;
import TestAutomation.helpers.Config;
import TestAutomation.helpers.WaitHelper;

public class DashboardPage {

	@FindBy(xpath = "//img[@class='avatar circle']")
	private WebElement userNameWidget;

	@FindBy(css = ".AppHeader-context-item-label")
	private WebElement mainHeading;

	public DashboardPage(Config testConfig) {
		PageFactory.initElements(testConfig.driver, this);
		WaitHelper.waitForPageLoad(testConfig, userNameWidget);
	}

	public void verifyDashboardPage(Config testConfig) {
		AssertHelper.compareEquals(testConfig, "Main Heading on Page", "Dashboard", mainHeading.getText());
	}
}