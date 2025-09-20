package TestAutomation;

import org.testng.annotations.Test;

import TestAutomation.helpers.ApiExecutionDetails.ApiDetails;
import TestAutomation.helpers.ApiJsonDetails.JsonDetails;
import TestAutomation.helpers.Config;
import TestAutomation.helpers.TestBase;
import TestAutomation.helpers.ProjectHelper;
import io.restassured.response.Response;

public class TestUserApis extends TestBase {
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying get user details api")
	public void testGetUserDetails(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
	}
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying get users list api")
	public void testGetUsersList(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USERS_LIST, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USERS_LIST, null);
	}
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying create user api")
	public void testCreateUser(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.CREATE_USER, JsonDetails.UserLogin);
        projectHelper.verifyApiResponse(response, ApiDetails.CREATE_USER, JsonDetails.UserLogin_Valid);
	}
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying update user api")
	public void testUpdateUser(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        // First get a user to update
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
        
        // Then update the user
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.UPDATE_USER, JsonDetails.UserUpdate);
        projectHelper.verifyApiResponse(response, ApiDetails.UPDATE_USER, JsonDetails.UserUpdate_Valid);
	}
	
	@Test(dataProvider = "getTestConfig", description = "This testcase is verifying delete user api")
	public void testDeleteUser(Config testConfig) 
	{
        int userDetailsRowNum = 1;
        
        ProjectHelper projectHelper = new ProjectHelper(testConfig, userDetailsRowNum);
        // First get a user to delete
        Response response = projectHelper.executeRequestAndGetResponse(ApiDetails.GET_USER_DETAILS, null);
        projectHelper.verifyApiResponse(response, ApiDetails.GET_USER_DETAILS, JsonDetails.GetUserDetails_Valid);
        
        // Then delete the user
        response = projectHelper.executeRequestAndGetResponse(ApiDetails.DELETE_USER, null);
        projectHelper.verifyApiResponse(response, ApiDetails.DELETE_USER, null);
	}
}