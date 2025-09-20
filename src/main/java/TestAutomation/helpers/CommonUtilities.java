package TestAutomation.helpers;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * This class will contain all the random functions
 * @author MukeshR
 *
 */
public class CommonUtilities
{
	/**
	 * format the string as json
	 * 
	 * @param input
	 * @return formatted json string
	 */
	public static String formatStringAsJson(String input)
	{
		return new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(input));
	}
	
	/**
	 * convert json array to map
	 *
	 * @param jsonArray
	 *                - json array to be converted to map
	 * @param initialKey
	 *                - string value that needs to be added as prefix to all the
	 *                keys
	 * @return map
	 */
	public static HashMap<String, String> convertJsonArrayToHashMap(JSONArray jsonArray, String initialKey)
	{
		HashMap<String, String> hashmap = new HashMap<String, String>();
		for (int i = 0; i < jsonArray.length(); i++)
		{
			String key = "" + i;
			if (!StringUtils.isEmpty(initialKey))
			{
				key = initialKey + "." + key;
			}
			Object obj = jsonArray.get(i);
			if (obj instanceof JSONObject){
				JSONObject jsonobj = (JSONObject) obj;
				hashmap.putAll(convertJsonObjectToHashMap(jsonobj, key));
			} else if (obj instanceof String){
				hashmap.put(key, (String) obj);
			}
		}
		return hashmap;
	}
	/**
	 * convert a json to map
	 *
	 * @param jsonObject
	 *                - json to be converted to map
	 * @param initialKey
	 *                - string value which needs to be added as prefix to all the
	 *                keys in resultant map
	 * @return map
	 */
	public static HashMap<String, String> convertJsonObjectToHashMap(JSONObject jsonObject, String initialKey)
	{
		HashMap<String, String> hashmap = new HashMap<String, String>();
		Iterator<String> keysItr = jsonObject.keys();
		while (keysItr.hasNext())
		{
			String key = keysItr.next();
			Object value = jsonObject.get(key);
			if (value.toString().equals("null"))
			{
				value = "";
			}
			if (!StringUtils.isEmpty(initialKey))
			{
				key = initialKey + "." + key;
			}
			if (value instanceof String)
			{
				hashmap.put(key, (String) value);
			} else if (value instanceof JSONObject)
			{
				hashmap.putAll(convertJsonObjectToHashMap((JSONObject) value, key));
			} else if (value instanceof JSONArray)
			{
				hashmap.putAll(convertJsonArrayToHashMap((JSONArray) value, key));
			} else {
				hashmap.put(key, String.valueOf(value));
			}
		}
		return hashmap;
	}

	/**
	 * This Method is used to create folder at given path
	 *
	 * @param path
	 * @return
	 */
	public static boolean createFolder(String path)
	{
		File newdir = new File(path);
		boolean result = false;
		if (!newdir.exists())
		{
			try
			{
				Files.createDirectories(Paths.get(path));
				System.out.println("Directory created successfully : " + path);
				result = true;
			}
			catch (Exception se)
			{
				System.out.println("Exception while creating Directory : " + path);
				se.printStackTrace();
			}
		} 
		else
		{
			System.out.println("Directory: " + path + " already Exist");
			result = true;
		}
		return result;
	}
	
	public static String formatNumber(double number)
	{
		DecimalFormat formatter = new DecimalFormat("#,###");
		return formatter.format(number);
	}
	
	public static String formatNumber(String number)
	{
		return formatNumber(Double.parseDouble(number));
	}
	
	public static String getPublicIP()
	{
		BufferedReader in = null;
		try
		{
			URL whatismyip = URI.create("http://checkip.amazonaws.com").toURL();
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			System.out.println("Current Machine's IP Address:- " + ip);
			return ip;
		}
		catch (IOException ioe)
		{
			System.out.println("Unable to get the IP.");
			ioe.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/**
	 * Check List Contains Given String
	 *
	 * @param list
	 * @param stringToMatch
	 * @return true/false
	 */
	public static boolean listContainsString(List<String> list, String stringToMatch)
	{
		Iterator<String> iter = list.iterator();
		while (iter.hasNext())
		{
			String tempString = iter.next();
			if (tempString.contains(stringToMatch))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Mask the credit card data
	 *
	 * @param actual text
	 * @return text with masked card detail if present
	 */
	public static String maskSensitiveDetails(String message)
	{
		if(StringUtils.isEmpty(message))
			return "";
		String maskedcard = "$1********$2";
		Pattern cardPattern = Pattern.compile("\\b([0-9]{4})[0-9]{4,10}([0-9]{4})\\b");
		Matcher matcher = cardPattern.matcher(message);
		if (matcher.find()){
			return matcher.replaceAll(maskedcard);
		}
		return message;
	}
	

	public static String createFileInResultsDirectory(Config testConfig, String subDirectoryName)
	{
		String fileName = testConfig.getRunTimeProperty("resultsDirectory") + File.separator + subDirectoryName + File.separator + testConfig.testcaseName + "_" + DataGenerator.generateRandomAlphaNumericString(15) + "_" + new SimpleDateFormat("HH-mm-ss").format(new Date());
		createFolder(fileName.substring(0, fileName.lastIndexOf(File.separator)));
		return fileName;
	}
	
	public static File getResultsDirectory(Config testConfig) {
		File dest = new File(System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "html" + File.separator);
		return dest;
	}
	
	/**
	 * This function return the URL of a file on runtime depending on LOCAL or
	 * OFFICIAL Run
	 * 
	 * @param testConfig
	 * @param fileUrl
	 * @return
	 */
	public static String convertFilePathToHtmlUrl(String fileUrl) {
		String htmlUrl = "";
		htmlUrl = fileUrl.replace(File.separator, "/");
		return htmlUrl;
	}
}