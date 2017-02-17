package helpers;

import static com.jayway.restassured.RestAssured.given;

import java.util.Map;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class ApiAutomationHelper 
{
	private Config testConfig;
	
	public enum ContentType {
    	ANY,TEXT,JSON,XML,HTML,URLENC,BINARY,OTHERS;
    }
    
	public ApiAutomationHelper(Config testConfig) 
	{
		this.testConfig = testConfig;
	}
    
	/**
	 * Generic function to execute an API
	 * @param testConfig
	 * @param requestUrl - url of API
	 * @param requestType - get/post etc
	 * @param contentType - JSON/HTML/URLENC etc
	 * @param parametersMap - pass all the params needed to execute the API
	 * @param passParamsInBody - true/false, as per need
	 * @param logResultOutput - Pass true if that API is not redirecting to another URL and you want to print the logs
	 * @return
	 */
	public Response ExecuteAndGetResponse(Config testConfig, String requestUrl, String requestType, ContentType contentType, Map<String, String> parametersMap, Boolean passParamsInBody)
	{
		
		RequestSpecification reqspec = null;
		testConfig.logComment("*****************************************************************");
		
		switch(contentType)
		{
		case ANY:
			reqspec = given().contentType(com.jayway.restassured.http.ContentType.ANY);
			break;
		case TEXT:
			reqspec = given().contentType(com.jayway.restassured.http.ContentType.TEXT);
			break;
		case JSON:
			reqspec = given().contentType(com.jayway.restassured.http.ContentType.JSON);
			break;
		case XML:
			reqspec = given().contentType(com.jayway.restassured.http.ContentType.XML);
			break;
		case HTML:
			reqspec = given().contentType(com.jayway.restassured.http.ContentType.HTML);
			break;
		case URLENC:
			reqspec = given().contentType(com.jayway.restassured.http.ContentType.URLENC);
			break;
		case BINARY:
			reqspec = given().contentType(com.jayway.restassured.http.ContentType.BINARY);
			break;
		case OTHERS:
			reqspec = given().contentType("application/x-www-form-urlencoded;charset=utf-8");
			break;
		}

		//Pass all the parameters
		if(passParamsInBody)
			reqspec.body(parametersMap.get("params"));
		else if(parametersMap != null)
			reqspec = reqspec.parameters(parametersMap);
		
		// Log the request details
			reqspec = reqspec.log().all();
		
		// Execute API
		reqspec = reqspec.when();
		
		Response response = null;
		switch(requestType.toLowerCase())
		{
		case "get":
			response = reqspec.get(requestUrl);
			break;
		case "post":
			response = reqspec.post(requestUrl);
			break;
		case "delete":
			response = reqspec.delete(requestUrl);
			break;
		case "put":
			response = reqspec.put(requestUrl);
			break;
		}

		response = response
				.then()
				.extract()
				.response();

		//Code to print the response, in case response is Empty then print the redirection URL
		String responseData = response.asString();
		if(responseData.equals(""))
			responseData = response.getHeader("location");
		
		testConfig.logComment("API Response for " + requestUrl + " :- "+ responseData);
		return response;
	}
}