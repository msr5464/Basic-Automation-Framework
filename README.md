# Test Automation Framework
This is Hybrid Automation Framework created using properties of `Page Object Model` and `Data Driven` automation frameworks.
The framework is created using the latest Selenium 3 features with Java, TestNG and Maven for Web-based automation, integrated with Appium for Mobile App automation and also integrated with Rest Assured for API Automation execution.


This is the list of tools, being used in this framework:
1. Apache Maven 4.0.0
2. Java 8
3. Selenium Webdriver 3.11.0
4. TestNG Framework 6.8.8
5. Rest Assured Framework 3.2.0
6. Chrome Web & Mobile View
7. Firefox Web & Mobile View
8. Email Notifications


# What is Test Automation Framework?
A “Test Automation Framework” is scaffolding that is laid to provide an execution environment for the automation test scripts. The framework provides the user with various benefits that help them to develop, execute and report the automation test scripts efficiently. It is more like a system that has created specifically to automate our tests.

In a very simple language, we can say that a framework is a constructive blend of various guidelines, coding standards, concepts, processes, practices, project hierarchies, modularity, reporting mechanism, test data injections etc. to pillar automation testing. Thus, the user can follow these guidelines while automating application to take advantages of various productive results.

The advantages can be in different forms like the ease of scripting, scalability, modularity, understandability, process definition, re-usability, cost, maintenance etc. Thus, to be able to grab these benefits, developers are advised to use one or more of the Test Automation Framework.


# What is the Page Object Model?
The Page Object Model is a design pattern of testing, derived from the Object Oriented Programming concepts. The POM describes the web application into the number of web pages being used and contains the elements as properties and actions as methods. This offers you low maintenance on the tests developed.

![Alt text](https://solutionscafe.files.wordpress.com/2014/01/untitled10.png "Page Object Model Example")


# MAIN FEATURES OF THIS PROJECT
1. Use of config.properties file
2. Use of DataProvider to supply testdata
3. Taking Screenshot on test failure
4. Use of SoftAssert to run full test case
5. Excel Sheet Reader to read testdata
6. Supports execution on Multiple Browsers
7. Proper Reporter Logging
8. API Automation integrated (check ApiAutomationHelper.java)
9. Contains 1 demo test script(testcase) of verifying login flow of ‘github.com’ website


# CODE STRUCTURE
1. Helpers : This package will contain all the Helper classes which can be used throughout the automation project.(Framework related classes)

2. PageObjects : This package contains classes representing each page which need to be automated.

3. Tests : This package contains all the test scripts(test cases) which are currently automated in different classes.(Like- TestLoginFlows.java)


# EXECUTION STEPS
1. Import this project in eclipse as “Existing Maven Project”
2. Install TestNG plugin in EcLipse
3. Go to TestLoginFlows.java and Run the single testcase present there
4. After execution check the output

# OUTPUT
Output can be seen out in index.html file present in test-output folder, or directly in the Eclipse console.


# FURTHER IMPLEMENTATIONS
Now, you can take hints from the test script(testcase) present in TestLoginFlows.java file and as per your own needs create required testcases, after creating similar classes in 'PageObjects' and 'Tests' packages.
