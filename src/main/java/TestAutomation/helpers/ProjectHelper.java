package TestAutomation.helpers;

import java.util.HashMap;

import org.json.JSONArray;
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
		case USER_SIGNUP:
			AssertHelper.compareEquals(testConfig, "API response code", 201, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "status", true, jsonObj.get("status"));
			AssertHelper.compareEquals(testConfig, "type", "Command execution success!", jsonObj.get("type"));
			AssertHelper.compareEquals(testConfig, "message", "Successfully registered.", jsonObj.get("message"));
			
			JSONObject innerJson = new JSONObject(jsonObj.get("data").toString());
			AssertHelper.compareEquals(testConfig, "role of innerJson", testData.get("role"), innerJson.get("role"));
			AssertHelper.compareEquals(testConfig, "scope of innerJson", testData.get("scope"), innerJson.get("scope"));
			AssertHelper.compareEquals(testConfig, "gender innerJson", testData.get("gender"), innerJson.get("gender"));
			AssertHelper.compareEquals(testConfig, "username of innerJson", testData.get("username"), innerJson.get("username"));
			AssertHelper.compareEquals(testConfig, "first_name of innerJson", testData.get("first_name"), innerJson.get("first_name"));
			AssertHelper.compareEquals(testConfig, "middle_name of innerJson", testData.get("middle_name"), innerJson.get("middle_name"));
			AssertHelper.compareEquals(testConfig, "last_name of innerJson", testData.get("last_name"), innerJson.get("last_name"));
			AssertHelper.compareEquals(testConfig, "locale of innerJson", "USA", innerJson.get("locale"));
			AssertHelper.compareEquals(testConfig, "profile of innerJson", "N/A", innerJson.get("profile"));
			break;
		case USER_LOGIN:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "status", true, jsonObj.get("status"));
			AssertHelper.compareEquals(testConfig, "type", "Command execution success!", jsonObj.get("type"));
			AssertHelper.compareEquals(testConfig, "message", "Login successful.", jsonObj.get("message"));
			
			innerJson = new JSONObject(jsonObj.get("data").toString());
			String id_token = innerJson.get("id_token").toString();
			testConfig.putRunTimeProperty("Authorization", "Bearer " + id_token);
			break;
		case USER_UPDATE:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "status", true, jsonObj.get("status"));
			AssertHelper.compareEquals(testConfig, "type", "Command execution success!", jsonObj.get("type"));
			AssertHelper.compareEquals(testConfig, "message", "Successfully updated", jsonObj.get("message"));
			
			innerJson = new JSONObject(jsonObj.get("data").toString());
			AssertHelper.compareEquals(testConfig, "API response code of innerJson", 200, innerJson.get("code"));
			AssertHelper.compareEquals(testConfig, "status of innerJson", true, innerJson.get("status"));
			AssertHelper.compareEquals(testConfig, "type of innerJson", "Command execution success!", innerJson.get("type"));
			AssertHelper.compareEquals(testConfig, "message of innerJson", "Successfully updated attributes.", innerJson.get("message"));
			break;
		case GET_USER_DETAILS:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "status", true, jsonObj.get("status"));
			AssertHelper.compareEquals(testConfig, "type", "Command execution success!", jsonObj.get("type"));
			AssertHelper.compareEquals(testConfig, "message", "User details found.", jsonObj.get("message"));
			
			JSONArray jsonArrObj = new JSONArray(jsonObj.get("data").toString());
			for(int arrSize = 0; arrSize < jsonArrObj.length(); arrSize++)
			{
				innerJson = jsonArrObj.getJSONObject(arrSize);
				AssertHelper.compareEquals(testConfig, "role of innerJson", testData.get("role"), innerJson.get("role"));
				AssertHelper.compareEquals(testConfig, "scope of innerJson", testData.get("scope"), innerJson.get("scope"));
				AssertHelper.compareEquals(testConfig, "gender innerJson", testData.get("gender"), innerJson.get("gender"));
				AssertHelper.compareEquals(testConfig, "username of innerJson", testData.get("username"), innerJson.get("username"));
				AssertHelper.compareEquals(testConfig, "first_name of innerJson", testData.get("first_name"), innerJson.get("first_name"));
				AssertHelper.compareEquals(testConfig, "middle_name of innerJson", testData.get("middle_name"), innerJson.get("middle_name"));
				AssertHelper.compareEquals(testConfig, "last_name of innerJson", testData.get("last_name"), innerJson.get("last_name"));
			}
			break;
		case GET_USER_SUB:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "status", true, jsonObj.get("status"));
			AssertHelper.compareEquals(testConfig, "type", "Command execution success!", jsonObj.get("type"));
			AssertHelper.compareEquals(testConfig, "message", "Successfully get user.", jsonObj.get("message"));
			
			innerJson = new JSONObject(jsonObj.get("data").toString());
			AssertHelper.compareEquals(testConfig, "email_verified of innerJson", "false", innerJson.get("email_verified"));
			AssertHelper.compareEquals(testConfig, "email of innerJson", testData.get("username"), innerJson.get("email"));
			AssertHelper.compareEquals(testConfig, "name of innerJson", testData.get("first_name")+" "+testData.get("last_name"), innerJson.get("name"));
			testConfig.putRunTimeProperty("sub", innerJson.get("sub"));
			break;
		case CONFIRM_USER_SIGNUP:
			AssertHelper.compareEquals(testConfig, "API response code", 200, response.getStatusCode());
			AssertHelper.compareEquals(testConfig, "status", true, jsonObj.get("status"));
			AssertHelper.compareEquals(testConfig, "type", "Command execution success!", jsonObj.get("type"));
			AssertHelper.compareEquals(testConfig, "message", "User confirmation success!", jsonObj.get("message"));
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
