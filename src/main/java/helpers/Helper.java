package helpers;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper 
{
	
	/**
	 * Compare two integer, double or float type values using a generic function.
	 * @param testConfig
	 * @param what
	 * @param expected
	 * @param actual
	 */
	public static <T> void compareEquals(Config testConfig, String what, T expected, T actual)
	{
		if (expected == null & actual == null)
		{
			testConfig.logPass(what, actual);
			return;
		}

		if (actual != null)
		{
			if (!actual.equals(expected))
			{
				testConfig.logFail(what, expected, actual);
			}
			else
			{
				testConfig.logPass(what, actual);
			}
		}
		else
		{
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
	public static void compareContains(Config testConfig, String what, String expected, String actual)
	{
		actual = actual.trim();
		if (actual != null)
		{
			if (!actual.contains(expected.trim()))
			{
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
			else
			{
				testConfig.logPass(what, actual);
			}
		}
		else
		{
			testConfig.logFail(what, expected, actual);
		}
	}
	

	/**
	 * Generate a random Alphabets string of given length
	 * 
	 * @param length - Length of string to be generated
	 */
	public static String generateRandomString(int length)
	{
		Random rd = new Random();
		String aphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
		{
			sb.append(aphaNumericString.charAt(rd.nextInt(aphaNumericString.length())));
		}
		return sb.toString();
	}
	

	/**
	 * Generate a random Alpha-Numeric string of given length
	 * 
	 * @param length - Length of string to be generated
	 */
	public static String generateRandomAlphaNumericString(int length)
	{
		Random rd = new Random();
		String aphaNumericString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
		{
			sb.append(aphaNumericString.charAt(rd.nextInt(aphaNumericString.length())));
		}
		return sb.toString();
	}
	
	
	/**
	 * Generate a random Special Character string of given length
	 * 
	 * @param length - Length of string to be generated
	 */
	public static String generateRandomSpecialCharacterString(int length)
	{
		Random rd = new Random();
		String specialCharString =  "~!@#$%^*()_<>?/{}[]|\";";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
		{
			sb.append(specialCharString.charAt(rd.nextInt(specialCharString.length())));
		}
		return sb.toString();
	}
	
	
	/**
	 * Generate a random decimal number
	 *
	 * @param integer lower bound value
	 * @param integer Upper bound value
	 * @param integer decimal points
	 *      
	 * @return an decimal number between that bound upto given decimal points
	 */
	public static String generateRandomDecimalValue(int lowerBound, int upperBound,int decimalPlaces) {
		Random random = new Random();
	    double dbl;
	    dbl = random.nextDouble()  * (upperBound - lowerBound) + lowerBound;
	    return String.format("%." + decimalPlaces + "f", dbl);
	}
	

	/**
	 * Generate a random number of given length
	 * 
	 * @param length - Length of number to be generated
	 * @return
	 */
	public static long generateRandomNumber(int length)
	{
		long randomNumber = 1;
		int retryCount = 1;

		// retryCount added for generating specified length's number
		while (retryCount > 0)
		{
			String strNum = Double.toString(Math.random());
			strNum = strNum.replace(".", "");

			if (strNum.length() > length)
			{
				strNum = strNum.substring(0, length);
			}
			else
			{
				int remainingLength = length - strNum.length() + 1;
				randomNumber = generateRandomNumber(remainingLength);
				strNum = strNum.concat(Long.toString(randomNumber));
			}

			randomNumber = Long.parseLong(strNum);
			if (String.valueOf(randomNumber).length() < length)
				retryCount++;
			else
				retryCount = 0;
		}
		return randomNumber;
	}
}
