package tests;

import org.testng.annotations.Test;

import helpers.Browser;
import helpers.Config;
import helpers.TestBase;
import pageObjects.DashboardPage;
import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.LoginPage.ExpectedLandingPageAfterLogin;

public class TestLoginFlows extends TestBase
{
	
	@Test(dataProvider="getTestConfig", description="This testcase is verifying successful login flow on 'github.com' website")
	public void testPositiveLoginFlow(Config testConfig)
	{
		//Row number of 'LoginDetails' sheet to get the username/password from login
		int loginDetailsSheetRowNumber = 1;
				
		//Launch Browser and Navigate to Home page of website
		Browser.openBrowserAndNavigateToUrl(testConfig, "https://github.com/");
		HomePage homePage = new HomePage(testConfig);
		
		//Navigate to Login Page
		LoginPage loginPage = (LoginPage) homePage.getLoginPage(testConfig);
		
		//Now Login and reach to Dashboard Page
		DashboardPage dashboardPage = (DashboardPage) loginPage.Login(testConfig, loginDetailsSheetRowNumber, ExpectedLandingPageAfterLogin.DashboardPage);
		
		//Verifying if Dashboard is loaded successfully or not
		dashboardPage.verifyDashboardPage(testConfig);
	}
}