package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class Appium 
{
	Config testConfig = null;
	public Appium(Config testConfig)
	{
		this.testConfig = testConfig;
		openMobileApplication(testConfig);
		
//TODO	AndroidMobileActions.allowOrDenyAllPermissionsOnAndroidApp(this.testConfig, permissionAllowButton, "Allow permission");
//TODO	MobileActions.hideKeyboard(testConfig);
	}
	
	public void openMobileApplication(Config testConfig)
	{
		if(testConfig.appiumDriver == null)
		{
			int tries=3;
			while(tries>0)
			{
				testConfig.appiumDriver = openApplication(testConfig);	
				if(testConfig.appiumDriver == null)
				{
					testConfig.logComment("=====>>APPLICATION not launched properly, Trying again...");
					tries--;
				}
				else
				{
					testConfig.logComment("MOBILE APPLICATION LAUNCHED SUCCESSFULLY");
					tries=0;
				}	
			}
			if(testConfig.appiumDriver == null)
			{
				testConfig.logFailToEndExecution("<-----APPLICATION not Launched properly, as appiumDriver is NULL ----->");
			}	
		}
	}
	
	public static AppiumDriver<MobileElement> openApplication(Config testConfig)
	{
		DesiredCapabilities capabilities = new DesiredCapabilities();
		String mobilePlatformName = testConfig.getRunTimeProperty("MobilePlatformName");
		switch(mobilePlatformName)
		{
			case "Android":
				String apkUrl = Helper.replaceArgumentsWithRunTimeProperties(testConfig, testConfig.getRunTimeProperty("APKUrl"));
		        String deviceName = testConfig.getRunTimeProperty("AndroidDeviceName");
		        String deviceVersion = testConfig.getRunTimeProperty("AndroidDeviceVersion");
		        String appPackage = testConfig.getRunTimeProperty("AppPackage");
		        String appActivity = testConfig.getRunTimeProperty("AppActivity");
		        String uninstallApk = testConfig.getRunTimeProperty("UninstallApk");
		        String appWaitActivity = testConfig.getRunTimeProperty("AppWaitActivity");
		        
		        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, mobilePlatformName);
		        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, deviceVersion);
		        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
		        capabilities.setCapability(MobileCapabilityType.APP,apkUrl);
		        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, appPackage);
		        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, appActivity);
		        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, "180");
		        capabilities.setCapability("unicodekeyboard", true);
		        capabilities.setCapability("resetkeyboard", true);
		        capabilities.setCapability("--no-reset", true);
		        
			    //    if (!deviceName.equalsIgnoreCase("device") && testConfig.remoteExecution)
	        	//capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, deviceName);
		        
		        if(uninstallApk.equalsIgnoreCase("true"))
		        	capabilities.setCapability("fullReset", true);

		        if(appWaitActivity != null)
		        	capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, appWaitActivity);
		        break;
		        
			case "iOS":
				deviceName = testConfig.getRunTimeProperty("deviceNameIOS");
		        String udid = testConfig.getRunTimeProperty("udid");
		        String bundleid = testConfig.getRunTimeProperty("bundleid");
		        String ipa = testConfig.getRunTimeProperty("ipa");
		        capabilities.setCapability("deviceName",deviceName);
		        String isSimulator = testConfig.getRunTimeProperty("simulator");
		        if(isSimulator == null || isSimulator.equalsIgnoreCase("no"))
		        	capabilities.setCapability("udid", udid);
		        capabilities.setCapability("bundleId", bundleid);
		        capabilities.setCapability("ipa", ipa);
		        String timeoutIOS = testConfig.getRunTimeProperty("newCommandTimeout");
		        if(timeoutIOS != null)
		        	capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, Integer.parseInt(timeoutIOS));
		        capabilities.setCapability("waitForAppScript", "$.delay(3000); $.acceptAlert();");
				break;
			default:
				testConfig.logFailToEndExecution("<-----Platform Type"+ mobilePlatformName +" Not defined----->");
		}

		AppiumDriver<MobileElement> driver = getAppiumDriver(testConfig, capabilities, mobilePlatformName);
		return driver;
	}
	
	private static AppiumDriver<MobileElement> getAppiumDriver(Config testConfig, DesiredCapabilities capabilities, String mobilePlatformName)
	{
		AppiumDriver<MobileElement> driver = null;
		int tries = 10;	
			while (tries > 0)
			{		
				try
				{
					if (testConfig.remoteExecution)
					{
						String remoteAddress = testConfig.getRunTimeProperty("RemoteAddress");
						String remotePort = testConfig.getRunTimeProperty("RemoteHostPort");
						testConfig.logComment("Start Execution on Remote : http://" + remoteAddress + ":" + remotePort);

						switch(mobilePlatformName)
						{
							case "Android":
								testConfig.androidDriver = new AndroidDriver<MobileElement>(new URL("http://"+ remoteAddress+":"+remotePort+"/wd/hub"), capabilities);
								driver = testConfig.androidDriver;
								break;
							case "iOS":
								testConfig.iosDriver = new IOSDriver<MobileElement>(new URL("http://"+ remoteAddress+":"+remotePort+"/wd/hub"), capabilities); 
								driver = testConfig.iosDriver;
								break;
							default:						
								driver= null;
								testConfig.logFailToEndExecution("<-----Platform Type"+ mobilePlatformName +" Not defined----->");
						}
					}
					else
					{
						String localAppiumAddress = "127.0.0.1";
						String localAppiumPort= "4723";
						testConfig.logComment("Start Execution on Local Machine: http://" + localAppiumAddress + ":" + localAppiumPort);

						switch(mobilePlatformName)
						{
							case "Android":
								startAppiumServer(testConfig,localAppiumAddress, localAppiumPort, mobilePlatformName);
								testConfig.androidDriver = new AndroidDriver<MobileElement>(new URL("http://" + localAppiumAddress + ":" + localAppiumPort+"/wd/hub"), capabilities);
								driver = testConfig.androidDriver;
								break;
							case "iOS":
								startAppiumServer(testConfig,localAppiumAddress, localAppiumPort, mobilePlatformName);
								testConfig.iosDriver = new IOSDriver<MobileElement>(new URL("http://" + localAppiumAddress + ":" + localAppiumPort+"/wd/hub"), capabilities);
								driver = testConfig.iosDriver;
								break;
							default:
								driver = null;
								testConfig.logFailToEndExecution("<-----Platform Type"+ mobilePlatformName +" Not defined----->");
						}
					}
					
					if (driver == null)
					{
						closeApplication(testConfig);
						tries--;
					}
					else
					{
						tries = 0;
					}
				}
				catch(Exception e)
				{
					testConfig.logComment("=====>>Exception in getAppiumDriver, now trying again.... : " + e.getMessage());
					closeApplication(testConfig);
					tries--;
				}
			}// End of While
			
			if (driver == null)
			{
				testConfig.logFailToEndExecution("<-----Unable to get AndoidDriver/iosDriver----->");
			}
			
			testConfig.putRunTimeProperty("MobileUAFlag", "true");
			testConfig.appiumDriver = driver;
			return driver;
	}
	
	    

	
	public void stopAppiumServer() 
	{
	    if (process != null) 
	        process.destroy();

	    System.out.println("Appium server stop");
	}
	
	static Process process = null;
	public static void startAppiumServer(Config testConfig, String localAppiumAddress, String localAppiumPort, String mobilePlatformName)
	{
		String ADB_listDevices = "adb devices";
		String ADB_serverKill = "adb kill-server";
		String ADB_serverStart = "adb start-server";
		String ADB_uninstallAPK = "adb uninstall ";
		String ADB_clearDataAPK = "adb shell pm clear ";
		
		String appPackageName = testConfig.getRunTimeProperty("appPackage");
		if(appPackageName != null)
		{
			ADB_uninstallAPK = ADB_uninstallAPK + appPackageName;
			ADB_clearDataAPK = ADB_clearDataAPK + appPackageName;
		}
		else
		{
			ADB_uninstallAPK = ADB_uninstallAPK + "com.payu.payutestapp";
			ADB_clearDataAPK = ADB_clearDataAPK + "com.payu.payutestapp";
		}
			
		/*if (mobilePlatformName.equalsIgnoreCase("Android"))
		{
			// Kill all node servers
			if(File.separator.equals("\\"))
				killAllActiveNodeServers(testConfig);
			
			// Restart adb server
			killAdbServer(testConfig);
			startAdbServer(testConfig);
			adbListDevices(testConfig);
			adbFetchAndroidOsVersion(testConfig);
			clearAPKData(testConfig);
		}*/
		
		Runtime runtime = Runtime.getRuntime();
		try
		{
			String osType = System.getProperty("os.name");
			if (!osType.startsWith("Window"))
			{
				// Start Appium Server on MAC OSX
				CommandLine command = new CommandLine("/Applications/Appium.app/Contents/Resources/node/bin/node");
				command.addArgument("/Applications/Appium.app/Contents/Resources/node_modules/appium/build/lib/main.js", false);
				command.addArgument("--address", false);
				command.addArgument(localAppiumAddress);
				command.addArgument("--port", false);
				command.addArgument(localAppiumPort);
				command.addArgument("--command-timeout",false);
				command.addArgument("7200");
				//command.addArgument("--platform-version",false);
				//command.addArgument(platformVersion);
				command.addArgument("--platform-name",false);
				command.addArgument(mobilePlatformName);
				command.addArgument("--show-ios-log",false);
				command.addArgument("--default-device",false);
				
				DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
				DefaultExecutor executor = new DefaultExecutor();
				executor.setExitValue(1);
				executor.execute(command, resultHandler);
			}
			else
			{
				// Start Appium Server on Windows Machine
				String appium_path = System.getenv("APPIUM_HOME");
				Process appiumServer = null;
				if (appium_path == null)
				{
					testConfig.logComment("Appium Home Has not been set");
					appiumServer = runtime.exec(ADB_serverStart+" -a "+localAppiumAddress+" -p "+localAppiumPort);
				}
				else
				{
					String commandToExecute = "\"" + appium_path + "/node.exe\" \"" + appium_path + "/node_modules/appium/bin/appium.js\" ";
					commandToExecute += " --log " + System.getProperty("user.dir") + "\\Appium.log ";// Log file
					commandToExecute += " -p " + localAppiumPort;// Port for appium to listen on
					commandToExecute += " -a " + localAppiumAddress;// address for appium port
					// commandToExecute += " -U " + "015d4b10091bee15";// To get a particular device
					
					testConfig.logComment("Running Command " + commandToExecute);
					appiumServer = runtime.exec(commandToExecute);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			testConfig.logComment("Error in Starting Appium Server: " + e.getMessage());
		}
	}
	
	/**Closes application/browser opened
	 * @param testConfig
	 */
	public static void closeApplication(Config testConfig)
	{
		try
		{
			String browser = testConfig.getRunTimeProperty("Browser");
			if (browser.equalsIgnoreCase("android_web"))
			{
				testConfig.logComment("Closing Browser");
				testConfig.appiumDriver.close();
			}
			else
			{
				testConfig.logComment("Closing Application");
				String removeAppAfterExecution = "yes";
 				if(removeAppAfterExecution != null && removeAppAfterExecution.equalsIgnoreCase("yes"))
				{
 					if(testConfig.iosDriver != null)
						//Removing ios app as reset is not working
						testConfig.iosDriver.removeApp(testConfig.getRunTimeProperty("bundleid"));
					else
						testConfig.androidDriver.removeApp(testConfig.getRunTimeProperty("appPackage"));
				}
				else
					testConfig.appiumDriver.closeApp();
			}
		}
		catch(Exception e)
		{
			testConfig.logComment("Unable to close App. Seems it already closed.");
		}
		
		try
		{
			testConfig.appiumDriver.quit();
			testConfig.appiumDriver = null;
			testConfig.iosDriver = null;
			testConfig.androidDriver = null;
			testConfig.driver = null;
		}
		catch(Exception e)
		{
			testConfig.appiumDriver = null;
			testConfig.iosDriver = null;
			testConfig.androidDriver = null;
			testConfig.driver = null;
		}
	}
	
	private static void executeCommand(String command) {
        String s = null;
        String abc = command.replace("\"", "");
        try {
            Process p = Runtime.getRuntime().exec(abc);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            System.out.println("exception happened: ");
            e.printStackTrace();
        }
    }

}