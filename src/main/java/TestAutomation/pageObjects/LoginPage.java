package TestAutomation.pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import TestAutomation.helpers.Config;
import TestAutomation.helpers.Element;
import TestAutomation.helpers.TestDataReader;
import TestAutomation.helpers.WaitHelper;

public class LoginPage {
	@FindBy(id = "login_field")
	private WebElement userNameTextBox;

	@FindBy(id = "password")
	private WebElement passwordTextBox;

	@FindBy(css = ".btn.btn-primary.btn-block")
	private WebElement signMeInBtn;

	public enum ExpectedLandingPageAfterLogin {
		HomePage, DashboardPage
	}

	public LoginPage(Config testConfig) {
		PageFactory.initElements(testConfig.driver, this);
		WaitHelper.waitForPageLoad(testConfig, userNameTextBox);
	}

	public Object Login(Config testConfig, int loginDetailsSheetRow, ExpectedLandingPageAfterLogin expectedLandingPage) {
		// Reading data from excel sheet and then getting logged in
		TestDataReader loginDetails = testConfig.getExcelSheet("LoginDetails");
		String username = loginDetails.getData(testConfig, loginDetailsSheetRow, "Username");
		String password = loginDetails.getData(testConfig, loginDetailsSheetRow, "Password");

		Element.enterData(testConfig, userNameTextBox, username, "UserName");

		Element.enterData(testConfig, passwordTextBox, password, "Password");

		Element.click(testConfig, signMeInBtn, "Sign In Button");

		switch (expectedLandingPage) {
		case HomePage:
			return this;

		case DashboardPage:
			return new DashboardPage(testConfig);

		default:
			return this;
		}
	}
}