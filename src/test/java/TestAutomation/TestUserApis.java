package TestAutomation;

import org.testng.annotations.Test;

import TestAutomation.helpers.ApiExecutionDetails.ApiDetails;
import TestAutomation.helpers.ApiJsonDetails.JsonDetails;
import TestAutomation.helpers.Config;
import TestAutomation.helpers.TestBase;
import TestAutomation.helpers.ProjectHelper;
import io.restassured.response.Response;

public class TestUserApis extends TestBase {
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying successful user login flow")
	public void testUserLogin(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_LOGIN, JsonDetails.UserLogin);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_LOGIN, JsonDetails.UserLogin_Valid);
	}
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying get user details api")
	public void testGetUserDetails(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_LOGIN, JsonDetails.UserLogin);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_LOGIN, JsonDetails.UserLogin_Valid);
        
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
	}
}