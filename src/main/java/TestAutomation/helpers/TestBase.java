package TestAutomation.helpers;

import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

/**
 * This class contains all the TestNG anotations related functions and Data providers
 * @author MukeshR
 *
 */
@Listeners(TestAutomation.helpers.TestListener.class)
public class TestBase {
	public static ThreadLocal<Config[]> threadLocalConfig = new ThreadLocal<Config[]>();

	@DataProvider(name = "getTestConfig")
	public Object[][] getTestConfiguration(Method method) {
		Config testConfig = new Config();
		testConfig.testcaseName = method.getName();

		threadLocalConfig.set(new Config[] { testConfig });
		return new Object[][] { { testConfig } };
	}

	@AfterMethod
	public void afterMethod() {
		Config[] testConfigs = threadLocalConfig.get();
		for (Config testConfig : testConfigs) {
			if (testConfig.driver != null) {
				testConfig.driver.quit();
				testConfig.logComment("Browser is closed now.");
			}
		}
	}
}