package TestAutomation.helpers;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class contains all the function related to JavaScript popups
 * @author MukeshR
 *
 */
public class Popup
{
	
	/**
	 * Cancel the Pop-up
	 * 
	 * @param Config - test config instance for driver instance to be used
	 */
	public static void cancel(Config testConfig)
	{
		Alert alert = getPopup(testConfig);
		
		if (alert != null)
		{
			alert.accept();
			testConfig.driver.switchTo().defaultContent();
			testConfig.logComment("Dismissed the Pop-up.");
		}
		
	}
	
	public static void confirmNoPopup(Config testConfig, String txt)
	{
		try
		{
			WebDriverWait wait = new WebDriverWait(testConfig.driver, Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime")));
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			// Alert alert = testConfig.driver.switchTo().alert();
			if (alert != null)
			{
				boolean result = true;
				if (alert.getText().equals(txt))
				{
					result = false;
					testConfig.driver.switchTo().alert();
					AssertHelper.compareTrue(testConfig, "Absence of Alert with text as:" + txt, result);
					alert.accept();
					return;
				}
				
				AssertHelper.compareTrue(testConfig, "Absence of Alert with text as: " + txt, result);
				return;
				
			}
			else
			{
				AssertHelper.compareTrue(testConfig, "Absence of Alert with text as: " + txt, true);
				return;
			}
		}
		catch (Exception e)
		{
			AssertHelper.compareTrue(testConfig, "Absence of Alert with text as: " + txt, true);
			return;
		}
		
	}
	
	public static Alert getPopup(Config testConfig)
	{
		try
		{
			Long ObjectWaitTime = Long.parseLong(testConfig.getRunTimeProperty("ObjectWaitTime"));
			WebDriverWait wait = new WebDriverWait(testConfig.driver, ObjectWaitTime);
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			alert = testConfig.driver.switchTo().alert();
			testConfig.logComment("Got the Alert with text '" + alert.getText() + "'");
			return alert;
		}
		catch(Exception e)
		{
			testConfig.logComment("Exception while getting the PopUp...");
			testConfig.logExceptionAndFail("", e);
			return null;
		}
	}
	
	public static boolean isAlertPresent(Config testConfig)
	{
		return isAlertPresent(testConfig, true);
	}
	
	public static boolean isAlertPresent(Config testConfig, boolean doLogging)
	{
		try
		{
			Alert alert = testConfig.driver.switchTo().alert();
			testConfig.logComment("Got the Alert with text '" + alert.getText() + "'");
			return true;
		}
		catch (Exception e)
		{
			if(doLogging)Log.Comment(testConfig, "Checked alert is not present");
			return false;
		}
	}
	
	/**
	 * Accept the Pop-up
	 * 
	 * @param Config
	 *            test config instance for driver instance to be used
	 */
	public static void ok(Config testConfig)
	{
		Alert alert = getPopup(testConfig);
		
		if (alert != null)
		{
			alert.accept();
			testConfig.driver.switchTo().defaultContent();
			testConfig.logComment("Accepted the Pop-up.");
		}
		
	}
	
	/**
	 * Get the Pop-up Text
	 * 
	 * @param Config
	 *            test config instance for driver instance to be used
	 * @return Pop-up Text
	 */
	public static String text(Config testConfig)
	{
		Alert alert = getPopup(testConfig);
		String text = alert.getText();
		return text;
	}
	
	/**
	 * Java method to type characters in windows file upload dialog boxes
	 * 
	 * @param characters
	 *            Filename to upload
	 * @param robot
	 */
	public static void type(String characters)
	{
		Robot robot = null;
		try
		{
			robot = new Robot();
		}
		catch (AWTException e)
		{
			e.printStackTrace();
		}
		robot.delay(9000);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		robot.delay(5000);
		StringBuffer newText = new StringBuffer();
		for (int i = 0; i < characters.length(); i++)
		{
			newText.append(characters.charAt(i));
			if (characters.charAt(i) == '\\')
			{
				if (characters.charAt(i + 1) == '\\')
				{
					i++;
				}
			}
		}
		StringSelection stringSelection = new StringSelection(newText.toString());
		clipboard.setContents(stringSelection, stringSelection);
		
		robot.delay(5000);
		robot.keyPress(KeyEvent.VK_CLEAR);
		// press CTRL+V
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		// release CTRL+V
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(5000);
		
		// Press Enter
		robot.keyPress(KeyEvent.VK_ENTER);
		// robot.keyPress(KeyEvent.VK_ENTER);
		// Release Enter
		// robot.keyRelease(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.delay(5000);
	}
	
	public void clearClipBoard()
	{
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		// clear clipboard
		StringSelection stringSelection = new StringSelection("");
		clipboard.setContents(stringSelection, stringSelection);
	}
	
	/*	*//**
	 * Java method to download a file to downloads folder
	 */
	/*
	 * public static void saveFileToDownloadsFolder(){ Robot robot = new
	 * Robot(); robot.keyPress(KeyEvent.VK_CLEAR); robot.delay(5000); // press
	 * ALT +s robot.keyPress(KeyEvent.VK_ALT); robot.keyPress(KeyEvent.VK_S);
	 * robot.keyRelease(KeyEvent.VK_S); robot.keyRelease(KeyEvent.VK_ALT); //
	 * press ALT+S robot.keyPress(KeyEvent.VK_ALT);
	 * robot.keyPress(KeyEvent.VK_O); robot.keyRelease(KeyEvent.VK_O);
	 * robot.keyRelease(KeyEvent.VK_ALT); // press ALT+S
	 * robot.keyPress(KeyEvent.VK_ALT); robot.keyPress(KeyEvent.VK_S);
	 * robot.keyRelease(KeyEvent.VK_S); robot.keyRelease(KeyEvent.VK_ALT);
	 * robot.delay(5000); // Press Enter robot.keyPress(KeyEvent.VK_ENTER); //
	 * Release Enter robot.keyRelease(KeyEvent.VK_ENTER); }
	 */
	/**
	 * Java method to get system clipboard contents
	 */
	public String getClipboardContents(WebElement el)
	{
		String returnText = null;
		
		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// get the contents on the clipboard in a
		// transferable object
		Transferable clipboardContents = systemClipboard.getContents(el);
		// check if clipboard is empty
		if (clipboardContents == null)
		{
			returnText = null;
		}
		else
			try
			{
				// see if DataFlavor of
				// DataFlavor.stringFlavor is supported
				if (clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor))
				{
					// return text content
					returnText = (String) clipboardContents.getTransferData(DataFlavor.stringFlavor);
					return returnText;
				}
			}
			catch (UnsupportedFlavorException ufe)
			{
				ufe.printStackTrace();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		return returnText;
	}
	
}
