package TestAutomation.helpers;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonSyntaxException;

import TestAutomation.helpers.ApiExecutionDetails.ApiDetails;
import TestAutomation.helpers.ApiExecutionDetails.ApiRequestType;
import TestAutomation.helpers.ApiExecutionDetails.Headers;
import TestAutomation.helpers.ApiJsonDetails.JsonDetails;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * This class contains the complete framework required to execute APIs using restassured
 * All the properties/parameters are being set using these functions
 * @author MukeshR
 *
 */
public class ApiHelper
{
	public HashMap<String, String> expectedTestData;
	public String[] sheetNames;
	public Config testConfig;
	public HashMap<String, String> testData;
	
	public ApiHelper(Config testConfig)
	{
		this.testConfig = testConfig;
	}

	/**
	 * this function will put the test data in run-time properties
	 */
	public void convertTestDataToRunTimeProperties()
	{
		for (String key : testData.keySet())
			testConfig.putRunTimeProperty(key, testData.get(key));
	}

	/**
	 * @param apiname
	 *                - object of class ApiDetails that contain all the information of the api
	 * @param jsonFile - json file if json body is to be posted
	 * @return
	 */
	public Response executeRequestAndGetResponse(ApiDetails apiDetails, Object jsonFile)
	{
		String body = getJsonData(jsonFile);
		HashMap<String, String> headers = getHeaders(apiDetails);
		HashMap<String, String> formData = getFormData(apiDetails);
		HashMap<String, String> params = getParams(apiDetails);
		return executeRequestAndGetResponse(apiDetails.getUrl(testConfig), apiDetails.getApiRequestType(), apiDetails.getApiContentType(), headers, params, formData, body);
	}

	/**
	 * initialize and set the headers, body, params, form params for the request
	 * based on api
	 *
	 * @param url
	 * @param apiRequestType
	 * @param apiContentType
	 * @param headers
	 * @param params
	 * @param formData
	 * @param body
	 * @return
	 */
	public Response executeRequestAndGetResponse(String url, ApiRequestType apiRequestType, ContentType apiContentType, HashMap<String, String> headers, HashMap<String, String> params, HashMap<String, String> formData, String body)
	{
		testConfig.logColorfulComment("Executing Api = " + apiRequestType + " " + url, "Blue");
		RequestSpecification reqspec = initialiseContentType(apiContentType, null);
		if (body != null){
			reqspec = reqspec.body(body);
			testConfig.logColorfulComment("Body = ", "Blue");
			testConfig.logCommentJson(body, "Blue");
		} else if (params != null){
			reqspec = reqspec.params(params);
			testConfig.logColorfulComment("Params = " + params, "Blue");
		} else if (formData != null){
			reqspec = reqspec.formParams(formData);
			testConfig.logColorfulComment("Form Data = " + formData, "Blue");
		}
		if (headers != null){
			reqspec = reqspec.headers(headers);
			testConfig.logColorfulComment("Headers = " + headers, "Blue");
		}
		return executeRequestAndGetResponse(url, apiRequestType, reqspec);
	}

	/**
	 * execute the request
	 *
	 * @param apiUrl
	 * @param apiRequestType
	 * @param reqspec
	 * @return response of the executed api
	 */
	public Response executeRequestAndGetResponse(String apiUrl, ApiRequestType apiRequestType, RequestSpecification reqspec)
	{
		Response response = null;

		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		try {
			switch (apiRequestType)
			{
			case GET:
				response = reqspec.when().get(apiUrl);
				break;
			case POST:
				response = reqspec.when().post(apiUrl);
				break;
			case DELETE:
				response = reqspec.when().delete(apiUrl);
				break;
			case PUT:
				response = reqspec.when().put(apiUrl);
				break;
			case PATCH:
				response = reqspec.when().patch(apiUrl);
				break;
			}
		}
		catch (Exception e){
			testConfig.logException("Api Execution failed, so trying again...", e);
			switch (apiRequestType)
			{
			case GET:
				response = reqspec.when().get(apiUrl);
				break;
			case POST:
				response = reqspec.when().post(apiUrl);
				break;
			case DELETE:
				response = reqspec.when().delete(apiUrl);
				break;
			case PUT:
				response = reqspec.when().put(apiUrl);
				break;
			case PATCH:
				response = reqspec.when().patch(apiUrl);
				break;
			}
		}
		testConfig.logComment("Api Response = ");
		try {
			testConfig.logCommentJson(CommonUtilities.formatStringAsJson(response.asString()), "Black");
		}
		catch (JsonSyntaxException jse){
			try {
				Document doc = Jsoup.parse(response.asString().replaceAll("window", "w"));
				if(!doc.getElementsByTag("form").isEmpty())
					testConfig.logColorfulComment(doc.toString(), "Black");
			} catch (Exception e) {	
			}
		}
		return response;
	}

	/**
	 * get the required form data for api in hashmap. the values of form params are
	 * extracted from runtime properties
	 *
	 * @param apiname
	 * @return
	 */
	public HashMap<String, String> getFormData(ApiDetails apiName)
	{
		if (apiName.getFormParams() == null)
			return null;
		HashMap<String, String> params = new HashMap<String, String>();
		String[] requiredRequestParams = apiName.getFormParams().split(",");
		for (String key : requiredRequestParams){
			String value = "";
			if (key.contains("=") && !key.startsWith("=") && !key.endsWith("=")){
				value = key.split("=")[1];
				key = key.split("=")[0];
			} else if(key.startsWith("=") || key.endsWith("=")){
				key = key.replace("=", "");
			} else {
				value = testData.get(key);
				value = StringUtils.isEmpty(value) ? testData.get(key) : value;
				value = StringUtils.isEmpty(value) ? testConfig.getRunTimeProperty(key) : value;
			}
			if (StringUtils.isEmpty(value)){
				value = "";
			}
			params.put(key, value);
		}
		return params;
	}

	/**
	 * get the required headers for api in hashmap
	 *
	 * @param apiname
	 * @return
	 */
	protected HashMap<String, String> getHeaders(ApiDetails apiname)
	{

		HashMap<String, String> headers = new HashMap<String, String>();
		for (Headers header : apiname.getRequiredHeaders()){
			String value = testData.get(header.name().toLowerCase());
			value = StringUtils.isEmpty(value) ? testData.get(header.name().toLowerCase()) : value;
			value = StringUtils.isEmpty(value) ? testConfig.getRunTimeProperty(header.name().toLowerCase()) : value;
			value = StringUtils.isEmpty(value) ? testConfig.getRunTimeProperty(header.name().toLowerCase()) : value;
			value = StringUtils.isEmpty(value) && header == Headers.Authorization ? testConfig.getRunTimeProperty("Authorization") : value;
			headers.put(header.name(), value);
		}
		return headers;
	

	}

	/**
	 * get the jsonbody
	 *
	 * @param jsonFile
	 * @return body for api
	 */
	public String getJsonData(Object jsonFile)
	{
		if (jsonFile == null)
			return null;
		String filePath;
		if (jsonFile instanceof JsonDetails){
			filePath = ((JsonDetails) jsonFile).getJsonFilePath(testConfig);
		} else {
			filePath = (String) jsonFile;
		}
		String jsonString = null;
		try {
			byte[] dataFromFile = FileUtils.readFileToByteArray(new File(filePath));
			jsonString = replaceArgumentsWithRunTimePropertiesForApi(new String(dataFromFile, Charset.defaultCharset()));
			jsonString = CommonUtilities.formatStringAsJson(jsonString);
		}
		catch (IOException e){
			testConfig.logExceptionAndFail("Exception occurred.", e);
		}
		return jsonString;
	}

	/**
	 * get the parameters from testData or runtime properties and return a hashmap
	 * containing the required values based on api
	 *
	 * @param apiName
	 * @return
	 */
	public HashMap<String, String> getParams(ApiDetails apiName)
	{
		if (apiName.getRequestParams() == null)
			return null;
		HashMap<String, String> params = new HashMap<String, String>();
		String[] requiredRequestParams = apiName.getRequestParams().split(",");
		for (String key : requiredRequestParams){
			String value = testData.get(key);
			value = StringUtils.isEmpty(value) ? testData.get(key) : value;
			value = StringUtils.isEmpty(value) ? testConfig.getRunTimeProperty(key) : value;
			if (StringUtils.isEmpty(value)){
				continue;
			}
			params.put(key, value);
		}
		return params;
	}

	/**
	 * initialize the content-type
	 *
	 * @param contentType
	 * @param reqspec
	 * @return
	 */
	public RequestSpecification initialiseContentType(ContentType contentType, RequestSpecification reqspec)
	{
		reqspec = reqspec == null ? given() : reqspec; // initialize request if null
		reqspec = contentType == null || contentType.equals(ContentType.ANY) ? reqspec : reqspec.contentType(contentType); // ignore content-type is it's null or */*
		reqspec = contentType != null && contentType.equals(ContentType.JSON) ? reqspec.accept("application/json") : reqspec; // add accept if content-type is json
		reqspec.relaxedHTTPSValidation();
		reqspec.log().all();
		RestAssuredConfig config = new RestAssuredConfig();
		config = config.encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
		reqspec = reqspec.config(config);
		return reqspec;
	}

	/**
	 * replace the parameters with the values stored in testData or run time
	 * properties
	 *
	 * @param input
	 * @return
	 */
	public String replaceArgumentsWithRunTimePropertiesForApi(String input)
	{
		if (input.contains("{$")){
			int index = input.indexOf("{$");
			input.length();
			input.indexOf("}", index + 2);
			String key = input.substring(index + 2, input.indexOf("}", index + 2));
			String value = testData.get(key);
			value = StringUtils.isEmpty(value) ? testConfig.getRunTimeProperty(key) : value;
			value = StringUtils.isEmpty(value) ? "null" : value;
			if (value.equalsIgnoreCase("null")){
				input = input.replace("\"{$" + key + "}\"", value);
			}
			if (value.equalsIgnoreCase("{skip}")){
				value = "";
			}
			input = input.replace("{$" + key + "}", value);
			return replaceArgumentsWithRunTimePropertiesForApi(input);
		}
		return input;
	}
}