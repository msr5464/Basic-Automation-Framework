#AUTOMATION FRAMEWORK (SELENIUM + JAVA + TESTNG)

This is Hybrid framework created using properties of "Page Object Model" and "Data Driven" frameworks.

We are using Selenium, TestNG for Reporting, Java as a language to automate and Maven to manage all the dependencies throughout the project, all of these resources are open source.



#MAIN FEATURES OF PROJECT:

1. ==>> Use of Properties file
2. ==>> Use of DataProvider
3. ==>> Taking Screenshot on failure
4. ==>> Use of SoftAssert to run full test case
5. ==>> Excel Sheet Reader to read data
6. ==>> Supports Multiple Browsers
7. ==>> Proper Reporter Logging
8. ==>> Contains 1 demo test script(testcase) of verifying login flow of ‘github.com’ website


#CODE STRUCTURE:

1. Helpers : This package will contain all the Helper classes which can be used throughout the automation project.(Framework related classes)

2. PageObjects : This package contains classes representing each page which need to be automated.

3. Tests : This package contains all the test scripts(test cases) which are currently automated in different classes.(Like- TestLoginFlows.java)


#EXECUTION STEPS:

1. Import this project in eclipse as “Existing Maven Project”
2. Install TestNG plugin in EcLipse
3. Go to TestLoginFlows.java and Run the testcase
4. Data is fetched from TestDataSheet excel sheet present in /Parameters folder.


#OUTPUT:
Output can be seen out in index.html file present in test-output folder, or directly in the Eclipse console.
This is the Package Representation of our Automation Framework:
