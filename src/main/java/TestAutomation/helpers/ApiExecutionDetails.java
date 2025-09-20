package TestAutomation.helpers;

import java.text.MessageFormat;
import java.util.ArrayList;

import io.restassured.http.ContentType;

/**
 * This class all the details related to any Api list url, params, headers etc
 * @author MukeshR
 *
 */
public class ApiExecutionDetails
{
	
	/**
	 * Host where API is to be hit
	 */
	public enum ApiHost
	{
		ApiBaseUrl
	}
	/**
	 * Request type for API
	 */
	enum ApiRequestType
	{
		DELETE, GET, POST, PUT, PATCH
	}
	/**
	 * Headers required to hit the API
	 */
	public enum Headers
	{
		Authorization, Accept, apiKey
	}
	
	public enum ApiDetails 
	{
		GET_USER_DETAILS("/users/{$userId}", ApiHost.ApiBaseUrl, ApiRequestType.GET, null, null, null, Headers.Accept),
		GET_USERS_LIST("/users", ApiHost.ApiBaseUrl, ApiRequestType.GET, null, null, null, Headers.Accept),
		CREATE_USER("/users", ApiHost.ApiBaseUrl, ApiRequestType.POST, ContentType.JSON, null, null, Headers.Accept),
		UPDATE_USER("/users/{$userId}", ApiHost.ApiBaseUrl, ApiRequestType.PUT, ContentType.JSON, null, null, Headers.Accept),
		DELETE_USER("/users/{$userId}", ApiHost.ApiBaseUrl, ApiRequestType.DELETE, null, null, null, Headers.Accept);
		
		ContentType apiContentType;
		ApiHost apiHost;
		ApiRequestType apiRequestType;
		String formParams;
		String path;
		String requestParams;
		ArrayList<Headers> requiredHeaders;
		
		ApiDetails(String tempPath, ApiHost tempApiHost, ApiRequestType tempApiRequestType, ContentType tempApiContentType, String tempRequestParams, String tempFormParams, Headers... tempRequiredHeaders)
		{
			path = tempPath;
			apiHost = tempApiHost;
			apiRequestType = tempApiRequestType;
			apiContentType = tempApiContentType;
			formParams = tempFormParams;
			requestParams = tempRequestParams;
			requiredHeaders = new ArrayList<Headers>();
			for (Headers h : tempRequiredHeaders) {
				requiredHeaders.add(h);
			}
		}
		
		public ContentType getApiContentType()
		{
			return apiContentType;
		}
		
		
		public ApiHost getApihost()
		{
			return apiHost;
		}
		
		
		public ApiRequestType getApiRequestType()
		{
			return apiRequestType;
		}
		
		
		public String getFormParams()
		{
			return formParams;
		}
		
		
		public String getPath()
		{
			return path;
		}
		
		
		public String getRequestParams()
		{
			return requestParams;
		}
		
		
		public ArrayList<Headers> getRequiredHeaders()
		{
			return requiredHeaders;
		}
		
		
		public String getUrl(Config testConfig, Object... params)
		{
			String tempPath = path;
			if (tempPath.contains("{$")) {
				tempPath = testConfig.replaceArgumentsWithRunTimeProperties(path);
			}
			if (apiHost != null)
				return MessageFormat.format(testConfig.getRunTimeProperty(apiHost.toString()) + tempPath, params);
			return MessageFormat.format(tempPath, params);
		}
	}
}