package TestAutomation.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * This class contains all the functions which can be used to generate any random data
 * @author MukeshR
 *
 */
public class DataGenerator 
{
	/**
	 * Generate a random Alphabets string of given length
	 * 
	 * @param length - Length of string to be generated
	 */
	public static String generateRandomString(int length)
	{
		Random rd = new Random();
		String aphaNumericString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
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
		String aphaNumericString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
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
	public static String generateRandomDecimalValue(int lowerBound, int upperBound,int decimalPlaces){
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
		int totalCounts = 1;
		// retryCount added for generating specified length's number
		while (retryCount > 0 && totalCounts < 10){
			String strNum = Double.toString(Math.random());
			strNum = strNum.replace(".", "");

			if (strNum.length() > length){
				strNum = strNum.substring(1, length + 1);
			}
			else {
				int remainingLength = length - strNum.length() + 1;
				randomNumber = generateRandomNumber(remainingLength);
				strNum = strNum.concat(Long.toString(randomNumber));
			}

			try {
				randomNumber = Long.parseLong(strNum);	
			}
			catch(NumberFormatException e){
				randomNumber = 0;
			}
			if (String.valueOf(randomNumber).length() < length){
				retryCount++;
				totalCounts ++;
			}
			else {
				retryCount = 0;
			}
		}
		return randomNumber;
	}

	public static String getCurrentDateTime()
	{
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
	}

	public static String getCurrentDateTime(String format)
	{
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	public static String getCurrentDate(String format)
	{
		// get current date
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}

	// get current time in given format
	public static String getCurrentTime(String format)
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String currentTime = formatter.format(cal.getTime());
		return currentTime;
	}
}