package TestAutomation.helpers;

import java.util.HashMap;

import org.json.JSONObject;

import TestAutomation.helpers.ApiExecutionDetails.ApiDetails;
import TestAutomation.helpers.ApiJsonDetails.JsonDetails;
import io.restassured.response.Response;

/**
 * This class contains the complete framework required to execute APIs using restassured
 * All the properties/parameters are being set using these functions
 * @author MukeshR
 *
 */
public class ProjectHelper extends ApiHelper
{
	public ProjectHelper(Config testConfig, int... detailsRowNum)
	{
		super(testConfig);
		sheetNames = new String[]{ "UserDetails"};
		testData = new HashMap<String, String>();
		if (detailsRowNum != null)
		{
			int j = 0;
			for (int i : detailsRowNum)
			{
				if (i > -1)
				{
					TestDataReader testDataReader = testConfig.getExcelSheet(sheetNames[j]);
					testData.putAll(testDataReader.getTestDataForApi(testConfig, i));
				}
				j++;
			}
		}
		if(testData.get("username") != null)
			testConfig.putRunTimeProperty("username", testData.get("username"));
		else
			testData.put("username", testConfig.getRunTimeProperty("username")); 
	}
	
	
	/**
	 * to verify the api response.
	 *
	 * @param response
	 * @param apiDetails
	 * @param expectedJsonObject
	 * @return
	 */
	public Response verifyApiResponse(Response response, ApiDetails apiDetails, JsonDetails expectedJsonObject)
	{
		JSONObject jsonObj = new JSONObject(response.asString());
		switch (apiDetails)
		{
		case GET_USER_DETAILS:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.assertNotNull(testConfig, "id", jsonObj.get("id"));
			AssertHelper.assertNotNull(testConfig, "name", jsonObj.get("name"));
			AssertHelper.assertNotNull(testConfig, "username", jsonObj.get("username"));
			AssertHelper.assertNotNull(testConfig, "email", jsonObj.get("email"));
			AssertHelper.assertNotNull(testConfig, "phone", jsonObj.get("phone"));
			AssertHelper.assertNotNull(testConfig, "website", jsonObj.get("website"));
			
			// Store user ID for future API calls
			String userId = jsonObj.get("id").toString();
			testConfig.putRunTimeProperty("userId", userId);
			break;
		case GET_USERS_LIST:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.assertNotNull(testConfig, "users list", jsonObj);
			break;
		case CREATE_USER:
			AssertHelper.compareEquals(testConfig, "API response code", 201, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "name", testData.get("name"), jsonObj.get("name"));
			AssertHelper.compareEquals(testConfig, "username", testData.get("username"), jsonObj.get("username"));
			AssertHelper.compareEquals(testConfig, "email", testData.get("email"), jsonObj.get("email"));
			AssertHelper.assertNotNull(testConfig, "id", jsonObj.get("id"));
			break;
		case UPDATE_USER:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "name", testData.get("name"), jsonObj.get("name"));
			AssertHelper.compareEquals(testConfig, "username", testData.get("username"), jsonObj.get("username"));
			AssertHelper.compareEquals(testConfig, "email", testData.get("email"), jsonObj.get("email"));
			AssertHelper.assertNotNull(testConfig, "id", jsonObj.get("id"));
			break;
		case DELETE_USER:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			break;
		default:
			testConfig.logFail("Key-values are not being verified in the response");
			break;
		}
		if (jsonObj != null) {
			AssertHelper.compareJsonKeys(testConfig, expectedJsonObject.getExpectedJSONObject(testConfig), jsonObj, "JSON response Keys");
		}
		return response;
	}
}
