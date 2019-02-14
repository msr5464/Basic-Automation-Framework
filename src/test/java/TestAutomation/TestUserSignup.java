package TestAutomation;

import org.testng.annotations.Test;

import TestAutomation.helpers.ApiExecutionDetails.ApiDetails;
import TestAutomation.helpers.ApiJsonDetails.JsonDetails;
import TestAutomation.helpers.Config;
import TestAutomation.helpers.TestBase;
import TestAutomation.helpers.ProjectHelper;
import io.restassured.response.Response;

public class TestUserSignup extends TestBase {
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying successful user login flow")
	public void testUserLogin(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_LOGIN, JsonDetails.UserLogin);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_LOGIN, JsonDetails.UserLogin_Valid);
	}
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying successful user details updation flow")
	public void testUserUpdate(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_LOGIN, JsonDetails.UserLogin);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_LOGIN, JsonDetails.UserLogin_Valid);
        
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_UPDATE, JsonDetails.UserUpdate);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_UPDATE, JsonDetails.UserUpdate_Valid);
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
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying successful user signup flow")
	public void testUserSignup(Config testConfig) 
	{
        int userDetailsRowNum = 3;
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_SIGNUP, JsonDetails.UserSignup);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_SIGNUP, JsonDetails.UserSignup_Valid);

        response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_SUB, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_SUB, JsonDetails.GetUserSub_Valid);
        

        response = projectHelper.executeRequestAndGetResponse(ApiDetails.CONFIRM_USER_SIGNUP, null);
        projectHelper.verifyApiResponse(response, ApiDetails.CONFIRM_USER_SIGNUP, JsonDetails.ConfirmUserSignup_Valid);
	}
	
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying end to end flow for existing user")
	public void testEndToEndFlowForExistingUser(Config testConfig) 
	{
		int userDetailsRowNum1 = 1;
		int userDetailsRowNum2 = 2;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum1);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_LOGIN, JsonDetails.UserLogin);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_LOGIN, JsonDetails.UserLogin_Valid);
        
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
        

        projectHelper = new ProjectHelper(testConfig, userDetailsRowNum2);
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_UPDATE, JsonDetails.UserUpdate);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_UPDATE, JsonDetails.UserUpdate_Valid);
        
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
        
        //Reset details back to previous one
        projectHelper = new ProjectHelper(testConfig, userDetailsRowNum1);
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_UPDATE, JsonDetails.UserUpdate);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_UPDATE, JsonDetails.UserUpdate_Valid);
	}

	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying end to end flow for new user")
	public void testEndToEndFlowForNewUser(Config testConfig) 
	{
        int userDetailsRowNum = 3;
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        
        //Signup as new user
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_SIGNUP, JsonDetails.UserSignup);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_SIGNUP, JsonDetails.UserSignup_Valid);

        //Get Sub to confirm the user
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_SUB, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_SUB, JsonDetails.GetUserSub_Valid);
        
        //Approve the user
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.CONFIRM_USER_SIGNUP, null);
        projectHelper.verifyApiResponse(response, ApiDetails.CONFIRM_USER_SIGNUP, JsonDetails.ConfirmUserSignup_Valid);
        
        //Now, Login with this new user
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_LOGIN, JsonDetails.UserLogin);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_LOGIN, JsonDetails.UserLogin_Valid);
        
        //Fetch all user details
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
        
        //Now, update few details
        userDetailsRowNum = 4;
        projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.USER_UPDATE, JsonDetails.UserUpdate);
        projectHelper.verifyApiResponse(response, ApiDetails.USER_UPDATE, JsonDetails.UserUpdate_Valid);
        
        //Again fetch details to see if details are updated or not
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
	}
}