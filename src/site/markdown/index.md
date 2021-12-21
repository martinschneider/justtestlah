<p align="center">
  <img src="images/logo.svg" width="300">
</p>

JustTestLah! is an end-to-end UI testing framework for mobile and web applications. It follows a [BDD](https://martinfowler.com/bliki/GivenWhenThen.html) approach and allows testing on different platforms (Android, iOS and Web) using the same test scenarios.

JustTestLah's main aim is to make the configuration easy and the test code as simple, readable and maintainable as possible.

JustTestLah! is based on [Selenium](https://www.seleniumhq.org), [Appium](https://www.appium.io) and [Cucumber](https://cucumber.io).

## Quick start

```bash
git clone https://github.com/martinschneider/justtestlah.git
mvn install -DskipTests=true
cd justtestlah-demos
mvn test
```

This requires Google Chrome (see below for how to configure other browsers or execute mobile tests).

> 🇸🇬 lah ([Singlish](https://en.wikipedia.org/wiki/Singlish)) - Placed at the end of a phrase or sentence either for emphasis or reassurance.

<details>
  <summary>Full documentation</summary>

There are more demos available under `justtestlah-demos/demos`; please [create a pull request](https://github.com/martinschneider/JustTestLah/pulls) if you want to contribute more complex ones.

You can run all of them like this (from the `justtestlah-demos` directory):
```mvn test -DjtlProps=$(echo pwd)/demos/google_web.properties```. Depending on their configuration, some of the demos will require additional software (e.g. a web browser, an Appium server, OpenCV etc.)

### Configuration

The `jtlProps` file is the main source of configuration. It is recommended to explicitly pass it as a system property (if omitted it will be loaded from the classpath; in that case, it must be named `justtestlah.properties`):

```bash
mvn test -DjtlProps=/absolute/path/to/your/testabc.properties
```

The most important values are (see the demos for more):

```ini
# Platform to test on (Android, iOS, Web)
platform=

# The path to the Cucumber feature files
features.directory=

# Java package containing the Cucumber steps
steps.package=

# Java package containing the Page objects
pages.package=

# WEB settings
web.browser=chrome
web.headless=false
```

Each run will consume a single configuration and run tests for one application and platform.

### Android

To test a mobile app, you need to set up [Appium](https://appium.io) and [run an Appium server](http://appium.io/docs/en/about-appium/getting-started). Make sure there is at least one physical or virtual (Android emulator or iPhone simulator) device connected. For a demo, run: `mvn test -DjtlProps=$(echo pwd)/demos/stackoverflow_android.properties` (modify `android.appPath=` as needed).

```ini
mobile.appiumUrl=http://127.0.0.1:4723/wd/hub

android.appPackage=com.stackexchange.stackoverflow
android.appActivity=com.stackexchange.stackoverflow.MainActivity
android.appPath=/Users/martinschneider/stackoverflow.apk
```

### iOS

There is currently no public demo for iOS, primarily because the source code or builds for the iPhone simulator are not publicly available for most apps and [ipa builds need to be re-signed to work with Appium](http://appium.io/docs/en/drivers/ios-xcuitest-real-devices).

If you want to contribute a demo, [please create a pull request](https://github.com/martinschneider/JustTestLah/pulls).

That said, JustTestLah! can be ([and has been](https://www.youtube.com/watch?v=maJkvP_qk4A)) used to automate iOS apps.

### Other platforms

In theory, JustTestLah! can easily be modified to run tests on any platform Appium or Selenium supports. Again, pull requests are welcome.

## Use JustTestLah! in your projects

It's simple!

### Option 1: Using a Maven archetype

A fast way to get a working template project is using the JustTestLah! [Maven archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html):

```bash
mvn archetype:generate -DarchetypeGroupId=qa.justtestlah -DarchetypeArtifactId=justtestlah-quickstart
```

The above command creates a new directory (based on the `artifactId` you selected). Change to this directory and run `mvn test` to execute the sample tests.

By default, the sample project will only include the core functionality of JustTestLah! You can add additional modules by passing any of the following arguments:

```bash
-Dawsdevicefarm=true -Dbrowserstack=true -Dgalen=true -Dmobile=true -Dvisual=true
```

### Option 2: Manual setup using Maven

Add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-core</artifactId>
  <version>1.10-SNAPSHOT</version>
</dependency>
```

### Option 3: Manual setup using Gradle

Add the following to your `build.gradle`:

```yaml
compile group: 'qa.justtestlah', name: 'justtestlah-core', version: '1.10-SNAPSHOT'
```

### Option 4: Manual setup

Add `justtestlah-core-1.10-SNAPSHOT.jar` to your classpath.

## Page objects, steps and feature files

There are three main ingredients for tests in JustTestLah!:

- Page objects represent a UI element (a page, a dialog, a screen etc.).
- Step definitions use page objects to define the actions of a test. They form the building blocks to write
- feature files that represent the test scenarios.

Steps and page objects are designed to be highly re-usable.

Demo of a feature file:

```gherkin
Feature: Search and tags

@web
Scenario: Filter by tags
  Given I am on the homepage
  When I go to the tags page
  And I filter for "selenium"
  And I select the tag "selenium"
  And I select the first question
  Then the question is tagged with "selenium"

@web @android
Scenario: Use the search function
  Given I am on the homepage
  When I search for "selenium"
  And I select the first question
  Then the question is tagged with "selenium"
```

Demo of a step definition class:

```java
public class HomeSteps extends BaseSteps {
  private HomePage home;

  @Given("I am on the homepage")
  public void homepage() {
    home.load();
  }

  @When("I go to the tags page")
  public void goToTags() {
    home.navigateToTagsPage();
  }

  @When("I search for {string}")
  public void search(String query) {
    home.search(query);
  }
}
```

Demo of a page object:

```java
@Component
@Profile(Platform.WEB)
public class HomePage extends BasePage<HomePage> {

  private QuestionsPage questions;

  private TagsPage tags;

  public HomePage load() {
    open(configuration.getBaseUrl());
    return this;
  }

  public TagsPage navigateToTagsPage() {
    $("MENU_TAGS").click();
    return tags;
  }

  public QuestionsPage search(String query) {
    $("SEARCH_FIELD").sendKeys(query);
    $("SEARCH_BUTTON").should(appear).click();
    return questions;
  }
}
```

You can inject page objects in steps by declaring a field:

```java
HomePage home;
```

As long as the page object class extends `qa.justtestlah.base.BasePage` JustTestLah! (and [Spring](https://spring.io)) will take care of the rest. In the same way you can also use page objects inside other page objects.

### Platform-(in)dependent page objects

[Spring profiles](https://www.baeldung.com/spring-profiles) are used to identify which platforms (Android, iOS, Web) supports. Simply annotate the page object class with `@Profile` and pass an array of platforms as its argument:

```java
@Component
@Profile({ANDROID, WEB})
public class LoginPage extends BasePage<LoginPage>
```

Ideally, the same Java object can represent a page for all platforms, which is the case when the only differences are the UI locators (the locator keys are platform-independent; their platform-dependent values are only resolved at runtime).

When the above approach is insufficient, you can use different page object classes for different platforms. In this case, it can be helpful to have a base class containing common methods and subclass it for any platform-specific changes.

Ensure that there is precisely one page object class for each page/platform combination. Otherwise, Spring will throw an error during start-up.

## Configuration

As mentioned before, all configuration goes in a file called `justtestlah.properties`. Its path can be passed using the `jtlProps` system property:

```bash
-DjtlProps=/path/to/justtestlah.properties
```

If no path is specified, JusttestLah! will load the configuration from the classpath (in that case, the file must be named `justtestlah.properties`).

The following is a complete list of available properties. You don't need to specify all as most of them are optional.

```ini
# GENERAL settings
platform=web
pages.package=qa.justtestlah.examples.stackoverflow.pages
steps.package=qa.justtestlah.examples.stackoverflow.steps
features.directory=src/test/resources/features/stackoverflow
cucumber.report.directory=target/report/cucumber

# Galen
galen.enabled=false
galen.inject.locators=true
galen.report.directory=target/report/galen

# Template matching
opencv.enabled=false

# OCR
tesseract.datapath=

# Cloudprovider (`aws` or `browserstack`, the default is `local`)
cloudprovider=local

# WEB settings
web.baseUrl=https://www.stackoverflow.com
web.browser=chrome
web.headless=true

# MOBILE settings
mobile.appiumUrl=http://127.0.0.1:4723/wd/hub
mobile.deviceOrientation=portrait
mobile.newCommandTimeout=60

# ANDROID settings
android.appPackage=com.stackexchange.stackoverflow
android.appActivity=com.stackexchange.stackoverflow.MainActivity
android.appPath=/Users/martinschneider/stackoverflow.apk
android.deviceName=Google Nexus 6
android.adbExecTimeout=20000

# IOS settings
ios.appPath=
ios.deviceName=iPhone 6

# BROWSERSTACK settings (requires `cloudprovider=browserstack` and `justtestlah-browserstack` on the classpath)

# Browserstack username
browserstack.username=
# Browserstack access key
browserstack.accessKey=

# Optional settings, see https://www.browserstack.com/automate/capabilities
browserstack.project=
browserstack.build=
browserstack.debug=true
browserstack.appiumLogs=true
browserstack.video=true
browserstack.geoLocation=SG
browserstack.timezone=SG
browserstack.appium_version=1.13.0
browserstack.acceptSslCerts=true

# AWS DEVICEFARM settings (requires `cloudprovider=aws` and `justtestlah-awsdevicefarm` on the classpath)
# The arn of your AWS Devicefarm project (mandatory)
aws.projectArn=

# App package to use. If this value is empty, it will be created and uploaded to AWS Devicefarm before the test execution
aws.appPackageArn=

# Test package to use. If this value is empty, it will be created and uploaded to AWS Devicefarm before the test execution
aws.testPackageArn=

# Optional extra data
aws.extraDataArn=

# Fully-qualified path to the justtestlah-demos project (required to build the test package)
aws.demo.path=/Users/martinschneider/git/justtestlah/justtestlah-demos

# Name for the test package (must match <finalName> in the justtestlah-demos pom.xml)
aws.testpackage.name=justtestlah-awsdevicefarm

# Device filters (optional)
aws.minOsVersion=9.0
aws.maxOsVersion=
aws.osVersion=
aws.model=
aws.manufacturer=
aws.formFactor=PHONE
aws.waitForDevice=true

# Device configuration (optional)
aws.deviceLatitude=
aws.deviceLongitude=
aws.bluetooth=
aws.gps=
aws.nfc=
aws.wifi=
# set this to true if you use device slots
aws.runUnmetered=false

# Additional AWS Devicefarm configuration
aws.accountsCleanup=
aws.appPackagesCleanup=
aws.jobTimeOut=
aws.skipAppResign=
```

## Test runner

JustTestLah! uses [JUnit](https://junit.org) to execute the tests. All you need to do is add an empty class that extends `qa.justtestlah.JustTestLahTest`:

```java
public class TestRunner extends JustTestLahTest {}
```

Alternatively, you can also use the JUnit test runner directly:

```java
@RunWith(JustTestLahRunner.class)
public class SomeTestClass {}
```

The feature files and steps are automatically picked up from the locations provided in `justtestlah.properties`.

## Locators

Elements are identified by a unique `id`, a `css` or an `xpath` expression. `AccesibilityId` (for iOS) and `UIAutomator` (for Android) are also supported. Each element has a unique key (e.g. `SEARCH_FIELD`) mapped to its corresponding locator expression in a .`yaml` file.
For example, let's assume the page object for the home page is `demoproject.pages.HomePage` (under `/src/main/java`). Then the corresponding locators are expected in `/demoproject/pages/HomePage.yaml` (under `/src/main/resources`).

Locators for different platforms reside side by side in the same yaml file. Locators are grouped by page object rather than by platform.

Example of a locator YAML file:

```yaml
LOGIN_BUTTON:
  web:
    type: xpath
    value: "//BUTTON//SPAN[text()='Log in']"
  ios:
    type: accesibilityId
    value: login_page_login_button
  android:
    type: id
    value: com.thecarousell.Carousell:id/login_page_login_button
```

JustTestLah! will automatically resolve the correct locator for the current platform. In the above example, we can access the search field in the `HomePage` page object by calling `$("SEARCH_BUTTON")`. This will return an instance of `com.codeborne.selenide.SelenideElement`. See the [Selenide quick start](https://selenide.org/quick-start.html) for more details. Two caveats to take note of:

1. It is not possible to directly use elements in step definitions (only in page objects). This is by design as UI elements are meant to be encapsulated in the page objects.
2. While we wrap Selenide's `$` method for the locator handling, the methods you can call on the returned `SelenideElement` instances remains the same.

If omitted, the default type of locators is `css`.

### Placeholders

Locators can include both dynamic and static placeholders, which will be replaced by variables passed to the `$` method.

#### Static placeholders

You can think of static placeholders as variables. They can be defined in a file called `placeholder.properties` in the root of the `pages` package (specified as `pages.package` in `justtestlah.properties`), i.e. the same folder the locator YAML files are placed in.

This file continues key/value pairs in the following format:

```ini
SOME_KEY=someValue
PACKAGE_NAME=com.stackexchange.stackoverflow
```

One use-case, as shown above, can be to define the Android package name as a variable and use it in all `id`-based locators like this:

```yaml
POST_TAG:
  android:
    type: id
    value: ${PACKAGE_NAME}:id/question_view_item_tags
```

If you want to override static placeholders during runtime, you can pass an extra placeholder file by setting `locator.placeholders.file` to its absolute path in `justtestlah.properties`. In this case, for any placeholders which occur in both files (the one under `pages.package` and `locator.placeholders.file`), the latter one will override the former.

#### Dynamic placeholders

Sometimes, you might require a locator which depends on dynamic values only known at runtime. You can achieve this by putting `%s` as a placeholder in the locator and using the `$(String locatorKey, Object... params)` and `$$(String locatorKey, Object... params)` methods in `BasePage`.

Let's see an example:

```yaml
POST_TAG:
  web:
    type: xpath
    value: "//A[contains(@class,'post-tag') and contains(text(),'%s')]"
```

Calling `$("POST_TAG", "selenium")` will return an element matching the following Xpath expression: `//A[contains(@class,'post-tag') and contains(text(),'selenium')`.

## Test data handling

JustTestLah! supports loading test data from YAML files. Each test data entity is represented by a Java class (the model) and one or many YAML files which contain the actual test data. For example:

```java
@TestData
public class User {
  private String username;
  private String password;

  public User() {}

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
```

```yaml
user:
  username: myUsername
  password: myPassword
```

Note that the top-level key in the YAML file must match the name of the corresponding test data class specified as the value of its `@TestData` annotation. If omitted, JustTestLah! will use the (lower camel case) name of the class instead.

You can then load test data in your tests as easy as this:

```java
User defaultUser = testdata(User.class);
```

```java
User validUser = testdata(User.class, "validUser");
```

```java
User invalidUser = testdata(User.class, "userWithInvalidPassword");
```

The second parameter points to the test entity's name, which is the YAML file's filename. If omitted, it defaults to `default`. In the above example, you would have three YAML files: `default.yaml`, `validUser.yaml` and `userWithInvalidPassword`.

There are three configuration values for this feature:

```
testdata.enabled=
model.package=
testdata.filter=
```

Setting `testdata.enabled=true` enables the YAML test data resolution. The default is `false`.

`model.package` is mandatory and specifies the root package to scan for Java objects representing test entities (these classes must be annotated with `@TestData`).

`testdata.filter` allows restricting the path to scan for test data YAML files. If left empty everything matching `**/testdata/**/*.y*ml` (under `src/test/resources`) will be considered.

Please note that there is an additional check for the string `testdata` in the path of any testdata YAML files. Ensure that all testdata files are in a folder of that name or use `testdata` as a prefix or suffix in the filename.

## Test reports

JustTestLah! uses [Cucumber's online reporting feature](https://reports.cucumber.io/).

## Visual and layout testing

Make sure `justtestlah-visual-1.10-SNAPSHOT.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-visual</artifactId>
  <version>1.10-SNAPSHOT</version>
</dependency>
```

### Template matching

JustTestLah! allows locating elements using a template image:

```properties
opencv.enabled=true
```

```yaml
QUESTION_ICON:
  web:
    type: opencv
    value: questionIcon.png   
    threshold: 0.8
```

This locator strategy is scale-invariant (to some extent). The algorithm used to achieve this scales the target image (a screenshot of the device) up and down until either a match is found or a minimum (320) or maximum (3200) image width is reached. This works sufficiently well, but improvements are always welcome.

The closer the size of the template matches the size of the image on the screen the faster and more accurate the matching will be.

#### Matching threshold

The`findImage` method has an optional `threshold` parameter, which can be used to define the accuracy of a match. The possible values range from 0 (no match) to 1 (pixel-perfect match). The default is `0.8`.

#### Appium `findByImage`

Appium introduced the `findByImage` functionality which works similarly to JustTestLah!'s approach. It also uses OpenCV, but on the server-side, i.e. the instance running the Appium server, not the Appium client. In many set-ups client and server might be the same machine. However, in practice there are situations where you don't control the server (e.g. when using a cloud provider).

The biggest flaw (in my opinion) with Appium's current image locator is that it introduces an extra round-trip: First, the client requests a screenshot from the WebDriver, then it sends the same screenshot back to the WebDriver together with the template image to receive the matching result. The screenshot travels between server and client twice.

JustTestLah! only requests the screenshot from the WebDriver and performs everything else on the client-side. The bigger advantage is that this also works with non-Appium WebDriver implementation (e.g. Selenium).

### OCR

JustTestLah! integrates [Tesseract](https://github.com/tesseract-ocr/tesseract) to perform [Optical character recognition](https://en.wikipedia.org/wiki/Optical_character_recognition).

This requires `justtestlah-visual-1.10-SNAPSHOT.jar` on the classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-visual</artifactId>
  <version>1.10-SNAPSHOT</version>
</dependency>
```

JustTestLah! uses [Tess4J](http://tess4j.sourceforge.net/), a Java wrapper for Tesseract. You still need to [install native binaries on your machine](https://github.com/tesseract-ocr/tesseract/wiki#installation) and set the [tesseract datapath](https://github.com/tesseract-ocr/tesseract/wiki/Data-Files) in the `justtestlah.properties`:

Depending on your operating system, Tesseract version and installation details, this might look something like this:

#### Linux

```ini
tesseract.datapath=/usr/share/tesseract-ocr/4.00/tessdata
```

#### Mac OS

```ini
# Mac OS
tesseract.datapath=/usr/local/Cellar/tesseract/4.1.1/share/tessdata
```

To use this feature, simply autowire an instance of the `OCR` class and pass a `WebElement` to its `getText` method. If you don't pass an argument, OCR will be performed on a screenshot of the entire viewport.

Here is a demo where we get the text of the main logo on Google's search page:

```java
@Component
@Profile(WEB)
@ScreenIdentifier("SEARCH_FIELD")
public class GooglePage extends BasePage<GooglePage> {

  @Autowired private OCR ocr;

  public String getLogoText() {
    return ocr.getText($("LOGO"));
  }
}

```

In the step class, we can then perform a check like this:

```java
assertThat(googlePage.getLogoText()).isEqualTo("Google");
```

### Galen

Make sure `justtestlah-galen-1.10-SNAPSHOT.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-galen</artifactId>
  <version>1.10-SNAPSHOT</version>
</dependency>
```

JustTestLah! integrates the [Galen framework](https://galenframework.com). It can be enabled by setting `galen.enabled=true` in `justtestlah.properties`.

Like the properties-file holding the locator information, there is an (optional) spec file for each page object (in the same package as the Java class under src/main/resources).

Checks can be triggered by calling `checkLayout()` on any page object class. An HTML report is generated in the directory defined in `galen.report.directory` in `justtestlah.properties` (the default is `target/galen-reports/`).

```yaml
  SEARCH_FIELD:
    below LOGO
    centered horizontally inside viewport
    visible

  LOGO:
    above SEARCH_FIELD
    centered horizontally inside viewport
    width < 100% of SEARCH_FIELD/width
    visible
```

Note that you do not need to specify the `@objects` section in the Galen spec. This will be auto-generated during runtime based on the page object YAML file. You can refer to any UI element using its key.

See the [Galen documentation](https://galenframework.com/docs/reference-galen-spec-language-guide) for more examples.

## Cloud service integrations

JustTestLah! supports integrating with various cloud service provides.

### Browserstack

You can run tests against [BrowserStack](https://www.browserstack.com) by adding the following configuration in `justtestlah.properties`:

```properties
cloudprovider=browserstack

# Browserstack username
browserstack.username=
# Browserstack access key
browserstack.accessKey=

# Optional settings, see https://www.browserstack.com/automate/capabilities
browserstack.debug=true
browserstack.appiumLogs=true
browserstack.video=true
browserstack.geoLocation=SG
browserstack.networkProfile=
browserstack.customNetwork=
browserstack.timezone=SG
browserstack.appium_version=1.10-SNAPSHOT.0
```

Make sure `justtestlah-browserstack-1.10-SNAPSHOT.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-browserstack</artifactId>
  <version>1.10-SNAPSHOT</version>
</dependency>
```

Please note that BrowserStack is a paid service.

### Applitools

[Applitools](https://applitools.com) support has been removed from the JTL core from version 1.9. That said, it is straightforward to integrate Applitools using their [Java SDK](https://github.com/applitools/eyes.sdk.java4). Please note that their [license excludes commercial use](https://github.com/applitools/eyes.sdk.java4/blob/develop/LICENSE).

### AWS Devicefarm

You can run tests against [AWS Devicefarm](https://us-west-2.console.aws.amazon.com/devicefarm/) by adding the following configuration in `justtestlah.properties`:

```properties
cloudprovider=aws

# The arn of your AWS Devicefarm project (mandatory)
aws.projectArn=

# App package to use. If this value is empty, it will be created and uploaded to AWS Devicefarm before the test execution
aws.appPackageArn=

# Test package to use. If this value is empty, it will be created and uploaded to AWS Devicefarm before the test execution
#aws.testPackageArn=

# Optional extra data
aws.extraDataArn=

# Fully-qualified path to the justtestlah-demos project (required to build the test package)
aws.demo.path=

# Name for the test package (must match <finalName> in the justtestlah-demos pom.xml)
aws.testpackage.name=justtestlah-awsdevicefarm

# Device filters (optional)
aws.minOsVersion=9.0
aws.maxOsVersion=
aws.osVersion=
aws.model=
aws.manufacturer=
aws.formFactor=PHONE
aws.waitForDevice=true

# Optional device configuration
aws.deviceLatitude=
aws.deviceLongitude
aws.bluetooth=
aws.gps=
aws.nfc=
aws.wifi=
# set this to true if you use device slots
aws.runUnmetered=false

# Additional AWS Devicefarm configuration
aws.accountsCleanup=
aws.appPackagesCleanup=
aws.jobTimeOut=
aws.skipAppResign=
```

Make sure `justtestlah-awsdevicefarm-1.10-SNAPSHOT.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-awsdevicefarm</artifactId>
  <version>1.10-SNAPSHOT</version>
</dependency>
```

You can refer to [this article](https://medium.com/@mart.schneider/mobile-test-automation-using-aws-device-farm-6bcf825fa27d) for a more detailled description of how to tweak AWS Devicefarm.

Please note that AWS Devicefarm is a paid service.

## Build from source

JustTestLah! uses [Maven](https://maven.apache.org/).

Make sure JAVA_HOME is set correctly. Then run:

```bash
mvn clean install -Dmaven.home=...
```

To build JustTestLah! without executing its tests, you can run:

```bash
mvn clean install -DskipTests=true
```

Both commands will build JustTestLah! and install it into your local Maven repository.

## Used libraries

JustTestLah! makes use of various frameworks to make writing and executing tests as enjoyable and straightforward as possible.

- [Selenium](https://www.seleniumhq.org), the main test framework used by JustTestLah!
- [Appium](https://appium.io), an extension of Selenium for native mobile app testing
- [Cucumber](https://cucumber.io), the BDD framework
- [JUnit](https://junit.org), the unit testing framework (mostly used as the runner for the tests)
- [Selenide](https://selenide.org), a convenience wrapper around Selenium
- [AssertJ](https://joel-costigliola.github.io/assertj), fluent assertions for unit tests
- [OpenCV](https://opencv.org), used for image comparison
- [Spring](https://spring.io), IoC container for [dependency injection](https://medium.com/faun/leverage-springs-dependency-injection-for-ui-automation-e32d1d82f738) and some added "magic" behind the scenes
- [Galen](http://galenframework.com), used for layout based testing
- [Tesseract](https://github.com/tesseract-ocr/tesseract), used for Optical Charatcer Recognition (OCR)

## Requirements and known-issues

- JustTestLah! requires Java 10 or higher (and has been tested on all major Java releases from 10 to 17). Java 9 support has been dropped because of [JDK-8193802](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8193802) which isn't fixed on Java below 10.
- The demos and unit tests use [Google Chrome](https://www.google.com/chrome).
- `${maven.home}` must be set to build JustTestLah! (unless you skip the unit tests).

</details>

<details>
<summary>Contact and support</summary>
Please let me know about any feedback, questions or ideas for improvement.

[Martin Schneider - mart.schneider@gmail.com](mailto:mart.schneider@gmail.com)

[![Buy me a coffee](https://www.buymeacoffee.com/assets/img/custom_images/yellow_img.png)](https://www.buymeacoffee.com/mschneider)

</details>

<p align="center">
  <img src="images/start.gif">
</p>

