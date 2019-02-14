package TestAutomation.pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import TestAutomation.helpers.Config;
import TestAutomation.helpers.Element;
import TestAutomation.helpers.WaitHelper;

public class HomePage {

	@FindBy(linkText = "Sign in")
	private WebElement signInButton;

	public HomePage(Config testConfig) {
		PageFactory.initElements(testConfig.driver, this);
		WaitHelper.waitForPageLoad(testConfig, signInButton);
	}

	public LoginPage getLoginPage(Config testConfig) {
		Element.click(testConfig, signInButton, "Sign In Button");
		return new LoginPage(testConfig);
	}
}