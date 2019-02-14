package TestAutomation.helpers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class contains all the functions which can be used for verification purposes
 * @author MukeshR
 *
 */
public class AssertHelper {
	
	/**
	 * Compare two integer, double or float type values using a generic function.
	 * @param testConfig
	 * @param what
	 * @param expected
	 * @param actual
	 */
	public static <T> void compareEquals(Config testConfig, String what, T expected, T actual){
		if (expected == null & actual == null){
			testConfig.logPass(what, actual);
			return;
		}
		
		if (actual != null){
			if (!actual.equals(expected))
				testConfig.logFail(what, expected, actual);
			else
				testConfig.logPass(what, actual);
		}
		else{
			testConfig.logFail(what, expected, actual);
		}
	}
	
	/**
	 * Compare 2 strings to check if 'expected' string is present in 'actual' string
	 * @param testConfig
	 * @param what
	 * @param expected
	 * @param actual
	 */
	public static void compareContains(Config testConfig, String what, String expected, String actual){
		actual = actual.trim();
		if (actual != null){
			if (!actual.contains(expected.trim())){
				Pattern expectedPattern = Pattern.compile(expected.substring(0, 2));
				//getFirstMatchingPoint
				int findPoint = 0;
				Matcher m = expectedPattern.matcher(actual.trim());
				if (m.find())
					findPoint = m.start();
				else
					findPoint = -1;
				actual = actual.substring(0, findPoint) + "<br/>" + actual.substring(findPoint);
				testConfig.logFail(what, expected, actual);
			}
			else{
				testConfig.logPass(what, expected);
			}
		}
		else{
			testConfig.logFail(what, expected, actual);
		}
	}

	/** This method is used to compare a value to True. If the value is True, the test case passes else fails.
	 * @param testConfig
	 * @param what
	 * @param actual
	 */
	public static void compareTrue(Config testConfig, String what, boolean actual){
		if (!actual)
			testConfig.logFail("Failed to verify '" + what +"'");
		else
			testConfig.logPass("Verified '" + what +"'");
	}
	
	/** This method is used to compare a value to false. If the value is false, the test case passes else fails.
	 * @param testConfig
	 * @param what
	 * @param actual
	 */
	public static void compareFalse(Config testConfig, String what, boolean actual){
		if (!actual)
			testConfig.logPass("Verified '" + what +"'");
		else
			testConfig.logFail("Failed to verify '" + what +"'");	
	}
	
	public static void assertNull(Config testConfig, String description, Object object){
		if(object!=null)
		{
			testConfig.logFail("Failed to verify '" + description +"'");
		}
		else
		{
			testConfig.logPass("Verified '" + description +"'");
		}
	}
	public static void assertNotNull(Config testConfig, String description, Object object){
		if(object==null)
		{
			testConfig.logFail("Failed to verify '" + description +"'");
		}
		else
		{
			testConfig.logPass("Verified '" + description +"'");
		}
	}

	/** 
	 * compare the key stuctures of 2 JSONs
	 * @param testConfig
	 * @param expectedJson
	 * @param actualJson
	 * @param description
	 */
	public static void compareJsonKeys(Config testConfig, JSONObject expectedJson, JSONObject actualJson, String description){
		if (expectedJson == null) {
			if (actualJson == null) {
				testConfig.logPass("Verified '" + description + "'");
				return;
			}
			testConfig.logFail("Failed to verify '" + description + "'" + ". Actual JSON is not null. Expected is: '" + expectedJson + "' but Actual is: '" + CommonUtilities.formatStringAsJson(actualJson.toString()) + "'.");
			return;
		}
		if(expectedJson.keySet().isEmpty()) {
			if(actualJson.keySet().isEmpty()) {
				testConfig.logPass("Verified '" + description + "'");
				return;
			}
			testConfig.logFail("Failed to verify '" + description + "'" + ". Actual JSON is not empty. Expected has: '" + expectedJson.keySet() + "' but Actual has: '" + actualJson.keySet() + "'.");
			return;
		}
		List<String> exList = Arrays.asList(JSONObject.getNames(expectedJson));
		Collections.sort(exList);
		List<String> acList = Arrays.asList(JSONObject.getNames(actualJson));
		Collections.sort(acList);
		if (!acList.containsAll(exList)) {
			testConfig.logFail("Failed to verify '" + description + "'" + ". Actual JSON doesn't contain all expected keys. Expected has: '" + expectedJson.keySet() + "' but Actual has: '" + actualJson.keySet() + "'.");
			return;
		}
		for (String key : exList) {
			Object exVal = expectedJson.get(key);
			Object acVal = actualJson.get(key);
			if (exVal.getClass() != acVal.getClass()) {
				testConfig.logFail("Failed to verify '" + description + "'" + ". Classes of Actual and Expected JSONs are different. Expected is: '" + exVal.getClass() + "' but Actual is: '" + acVal.getClass() + "'."+key);
				return;
			}
			if (exVal instanceof JSONObject) {
				compareJsonKeys(testConfig, new JSONObject(exVal.toString()), new JSONObject(acVal.toString()), description);
			} else if (exVal instanceof JSONArray) {
				JSONArray exArr = new JSONArray(exVal.toString());
				JSONArray acArr = new JSONArray(acVal.toString());
				if (exArr.length() > 0) {
					Object e = exArr.get(0);
					for (Object a : acArr) {
						if (e.getClass() != a.getClass()) {
							testConfig.logFail("Failed to verify '" + description + "'" + ". Classes of actual and expected json array objects are different. Expected is: '" + e.getClass() + "' but Actual is: '" + a.getClass() + "'.");
							return;
						}
						if (a instanceof JSONObject)
							compareJsonKeys(testConfig, new JSONObject(e.toString()), new JSONObject(a.toString()), description);
					}
				} else {
					if (exArr.length() != acArr.length()) {
						testConfig.logFail("Failed to verify '" + description + "'" + ". Length of actual and expected json arrays is different. Expected is: '" + exArr.length() + "' but Actual is: '" + acArr.length() + "'.");
						return;
					}
				}
			} else if (acVal instanceof JSONObject || acVal instanceof JSONArray) {
				testConfig.logFail("Failed to verify '" + description + "'" + ". Actual is not json object or json array but it should be. Expected is: '" + CommonUtilities.formatStringAsJson(expectedJson.toString()) + "' but Actual is: '" + CommonUtilities.formatStringAsJson(actualJson.toString()) + "'.");
				return;
			}
		}
		testConfig.logPass("Verified '" + description + "'");
		return;
	}

}