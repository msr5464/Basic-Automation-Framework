# Selenium & RestAssured - Automation Framework
This is a modern Hybrid Automation Framework created using properties of `Page Object Model` and `Data Driven` automation frameworks.
The framework is built using the latest Selenium 4 features with Java 11, TestNG and Maven for Web-based automation, integrated with Appium for Mobile App automation and also integrated with Rest Assured for API Automation execution.

## 🚀 Latest Updates (2024)
- ✅ **Upgraded to Selenium 4.15.0** - Latest WebDriver features and improved performance
- ✅ **Updated to TestNG 7.9.0** - Enhanced test reporting and parallel execution
- ✅ **Upgraded to Rest Assured 5.3.2** - Modern API testing capabilities
- ✅ **Updated to Java 11** - Better performance and security
- ✅ **Fixed all deprecated code** - Future-proof and warning-free
- ✅ **Added JSONPlaceholder API integration** - Reliable API testing service
- ✅ **Modern Selenium 4.x API** - Using Duration instead of deprecated TimeUnit
- ✅ **Resource management improvements** - Proper try-with-resources implementation

## 🛠️ Technology Stack
1. **Apache Maven 3.11.0** - Build automation and dependency management
2. **Java 11** - Modern Java features and performance improvements
3. **Selenium WebDriver 4.15.0** - Latest browser automation capabilities
4. **TestNG Framework 7.9.0** - Advanced testing framework with parallel execution
5. **Rest Assured 5.3.2** - Powerful API testing library
6. **Apache POI 5.2.4** - Excel file handling for test data
7. **Chrome & Firefox** - Cross-browser testing support
8. **JSONPlaceholder API** - Free API testing service (no API key required)
9. **MongoDB Driver 4.11.1** - Database connectivity
10. **Appium 9.0.0** - Mobile app automation


# What is Test Automation Framework?
A “Test Automation Framework” is scaffolding that is laid to provide an execution environment for the automation test scripts. The framework provides the user with various benefits that help them to develop, execute and report the automation test scripts efficiently. It is more like a system that has created specifically to automate our tests.

In a very simple language, we can say that a framework is a constructive blend of various guidelines, coding standards, concepts, processes, practices, project hierarchies, modularity, reporting mechanism, test data injections etc. to pillar automation testing. Thus, the user can follow these guidelines while automating application to take advantages of various productive results.

The advantages can be in different forms like the ease of scripting, scalability, modularity, understandability, process definition, re-usability, cost, maintenance etc. Thus, to be able to grab these benefits, developers are advised to use one or more of the Test Automation Framework.


# What is the Page Object Model?
The Page Object Model is a design pattern of testing, derived from the Object Oriented Programming concepts. The POM describes the web application into the number of web pages being used and contains the elements as properties and actions as methods. This offers you low maintenance on the tests developed.

![Alt text](https://solutionscafe.files.wordpress.com/2014/01/untitled10.png "Page Object Model Example")


# 🌟 MAIN FEATURES OF THIS PROJECT

## Core Framework Features
1. **Configuration Management** - Flexible config.properties file system
2. **Data-Driven Testing** - TestNG DataProvider for dynamic test data
3. **Screenshot on Failure** - Automatic screenshot capture for debugging
4. **Soft Assertions** - Complete test execution with detailed reporting
5. **Excel Integration** - Apache POI for reading test data from Excel files
6. **Cross-Browser Support** - Chrome, Firefox with Selenium Manager
7. **Comprehensive Logging** - Detailed test execution logs and reports
8. **API Automation** - Rest Assured integration for API testing
9. **Mobile Testing** - Appium integration for mobile app automation

## API Testing Features
- **JSONPlaceholder Integration** - Free, reliable API testing service
- **CRUD Operations** - Create, Read, Update, Delete user operations
- **JSON Validation** - Comprehensive response validation
- **Dynamic Test Data** - Parameterized API testing
- **No API Key Required** - Ready-to-use API endpoints

## Demo Test Cases
- **Web UI Testing** - GitHub login flow automation
- **API Testing** - Complete user management API suite
  - Get user details
  - Get users list
  - Create new user
  - Update existing user
  - Delete user


# 📁 CODE STRUCTURE

## Package Organization
```
src/
├── main/java/TestAutomation/
│   ├── helpers/           # Framework utility classes
│   │   ├── Browser.java   # Browser management and WebDriver setup
│   │   ├── Element.java   # Web element interactions and actions
│   │   ├── WaitHelper.java # Explicit wait utilities
│   │   ├── ApiHelper.java # API testing utilities
│   │   ├── TestDataReader.java # Excel/CSV data reading
│   │   └── ...           # Other helper classes
│   └── pageObjects/      # Page Object Model classes
│       ├── LoginPage.java
│       ├── HomePage.java
│       └── DashboardPage.java
└── test/java/TestAutomation/
    ├── TestLoginFlows.java    # Web UI test cases
    └── TestUserApis.java      # API test cases
```

## Key Components
1. **Helpers Package** - Core framework utilities and reusable methods
2. **PageObjects Package** - Page Object Model implementation for web pages
3. **Tests Package** - Test case implementations using TestNG
4. **Parameters Package** - Configuration files and test data
5. **Drivers Package** - Browser driver executables


# 🚀 EXECUTION STEPS

## Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Chrome/Firefox browser installed

## Setup Instructions
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Basic-Automation-Framework
   ```

2. **Import project in your IDE**
   - **IntelliJ IDEA**: Open as Maven project
   - **Eclipse**: Import as "Existing Maven Project"
   - **VS Code**: Open folder and install Java extension pack

3. **Install TestNG plugin** (if using Eclipse)

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
# Web UI Tests
mvn test -Dtest=TestLoginFlows

# API Tests
mvn test -Dtest=TestUserApis
```

### Run with Specific Browser
```bash
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
```

## 📊 OUTPUT & REPORTS
- **TestNG Reports**: `target/surefire-reports/index.html`
- **Screenshots**: `test-output/screenshots/` (on test failures)
- **Console Output**: Detailed logs in IDE console
- **API Response Logs**: Detailed API request/response logging

## 🔧 Configuration
Update `Parameters/test.properties` for:
- Browser selection
- API endpoints
- Test data
- Timeout settings

# 🚀 FURTHER IMPLEMENTATIONS

## Adding New Test Cases
1. **Web UI Tests**: Create new test methods in `TestLoginFlows.java` or new test classes
2. **API Tests**: Add new test methods in `TestUserApis.java` or create new API test classes
3. **Page Objects**: Add new page classes in `pageObjects` package
4. **Helper Methods**: Extend existing helper classes or create new ones

## Best Practices
- Use Page Object Model for web UI tests
- Implement proper wait strategies
- Use data-driven testing with Excel/CSV
- Add proper assertions and error handling
- Follow naming conventions for test methods

## API Testing
The framework includes comprehensive API testing capabilities:
- **Base URL**: `https://jsonplaceholder.typicode.com`
- **No API Key Required**: Ready-to-use endpoints
- **CRUD Operations**: Full user management testing
- **JSON Validation**: Comprehensive response validation
