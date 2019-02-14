package TestAutomation.helpers;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class contains details related to Json files of request/response of any Api
 * @author MukeshR
 *
 */
public class ApiJsonDetails
{
	public enum JsonDetails
	{
		UserSignup("requestJson"),
		UserLogin("requestJson"),
		UserUpdate("requestJson"),
		ChangePassword("requestJson"),
		
		UserSignup_Valid("responseJson"),
		UserSignup_Invalid("responseJson"),
		UserLogin_Valid("responseJson"),
		UserUpdate_Valid("responseJson"),
		GetUserDetails_Valid("responseJson"),
		GetUserSub_Valid("responseJson"),
		ConfirmUserSignup_Valid("responseJson"),
		ChangePassword_Valid("responseJson"),
		UserDelete_Valid("responseJson");
		
		String requestOrResponseJson;
		JsonDetails(String requestOrResponse)
		{
			requestOrResponseJson = requestOrResponse;
		}
		
		public String getJsonFilePath(Config testConfig)
		{
			return System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "Json" + File.separator + requestOrResponseJson + File.separator + this + ".json";
		}

		
		public String getJsonFileData(Config testConfig) 
		{
			String filePath = getJsonFilePath(testConfig);
			String jsonString = "";
			try {
				byte[] dataFromFile = FileUtils.readFileToByteArray(new File(filePath));
				jsonString = new String(dataFromFile, Charset.defaultCharset());
			}
			catch (Exception e) {
				testConfig.logExceptionAndFail("", e);
			}
			return jsonString;
		}

		
		public JSONArray getExpectedJSONArray(Config testConfig)
		{
			try {
				return new JSONArray(getJsonFileData(testConfig));
			}
			catch (Exception e) {
				testConfig.logExceptionAndFail("", e);
			}
			return null;
		}

		
		public JSONObject getExpectedJSONObject(Config testConfig) 
		{
			try {
				return new JSONObject(getJsonFileData(testConfig));
			}
			catch (Exception e) {
				testConfig.logExceptionAndFail("", e);
			}
			return null;
		}
	}
}
