package pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import helpers.Browser;
import helpers.Config;
import helpers.Element;

public class LoginPage 
{
	@FindBy(id="user_email")
	private WebElement userNameTextBox;
	
	@FindBy(css=".btn.btn-default.button")
	private WebElement continueBtn;
	
	@FindBy(id="user_password")
	private WebElement passwordTextBox;
	
	@FindBy(id="user_submit")
	private WebElement signMeInBtn;
	
	
	public enum ExpectedLandingPageAfterLogin
	{
		HomePage, DashboardPage
	}
	
	public LoginPage(Config testConfig)
	{
		PageFactory.initElements(testConfig.driver, this);
		Browser.waitForPageLoad(testConfig, userNameTextBox);
	}
	
	public Object Login(Config testConfig, String username, String password, ExpectedLandingPageAfterLogin expectedLandingPage)
	{

		Element.enterData(testConfig, userNameTextBox, username, "UserName");
		Element.click(testConfig, continueBtn, "Continue Button");
		
		Browser.wait(testConfig, 2);
		
		Element.enterData(testConfig, passwordTextBox, password, "Password");
		Element.click(testConfig, continueBtn, "Continue Button");
		
		switch(expectedLandingPage)
		{
			case HomePage:
				return this;
				
			case DashboardPage:
				return new DashboardPage(testConfig);
				
			default:
				return this;
		}
	}
}