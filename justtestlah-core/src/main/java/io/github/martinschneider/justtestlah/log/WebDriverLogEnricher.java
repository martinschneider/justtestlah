package io.github.martinschneider.justtestlah.log;

import io.github.martinschneider.justtestlah.configuration.PropertiesHolder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetches logs from the WebDriver (server) and attaches them to the current (client) log.
 *
 * <p>logTypes can be configured in justtestlah.properties:
 *
 * <p>e.g. additionalLogTypes=server,logcat
 *
 * <p>For Appium, this requires the server to be started with `appium --relaxed-security`.
 */
public class WebDriverLogEnricher implements WebDriverEventListener {

  public WebDriverLogEnricher() {
    String property = new PropertiesHolder().getOptionalProperty("additionalLogTypes");
    if (property != null && !property.isEmpty()) {
      logTypes.addAll(Arrays.asList(property.split(",")));
    }
  }

  // filter to match log lines related to fetching the log
  private static final String REGEXP_FILTER =
      ".*Retrieving .* log.*|.*Calling AppiumDriver.getLog.*|Responding to client with driver.getLog.*|.*POST.*log.*|.*\\{\"type\":\"server\"\\}.*";

  private static final Logger LOG = LoggerFactory.getLogger("webdriver");

  private List<String> logTypes = new ArrayList<String>();

  private void appendWebDriverLog(WebDriver driver) {
    for (String logType : logTypes) {
      for (LogEntry log : driver.manage().logs().get(logType)) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        String message = log.getMessage();
        // filter out messages related to fetching the log
        if (!message.matches(REGEXP_FILTER)) {
          LOG.info("{} {} {}", logType, formatter.format(log.getTimestamp()), log.getMessage());
        }
      }
    }
  }

  @Override
  public void beforeAlertAccept(WebDriver driver) {}

  @Override
  public void afterAlertAccept(WebDriver driver) {

    appendWebDriverLog(driver);
  }

  @Override
  public void afterAlertDismiss(WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeAlertDismiss(WebDriver driver) {}

  @Override
  public void beforeNavigateTo(String url, WebDriver driver) {}

  @Override
  public void afterNavigateTo(String url, WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeNavigateBack(WebDriver driver) {}

  @Override
  public void afterNavigateBack(WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeNavigateForward(WebDriver driver) {}

  @Override
  public void afterNavigateForward(WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeNavigateRefresh(WebDriver driver) {}

  @Override
  public void afterNavigateRefresh(WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeFindBy(By by, WebElement element, WebDriver driver) {}

  @Override
  public void afterFindBy(By by, WebElement element, WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeClickOn(WebElement element, WebDriver driver) {}

  @Override
  public void afterClickOn(WebElement element, WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeChangeValueOf(
      WebElement element, WebDriver driver, CharSequence[] keysToSend) {}

  @Override
  public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeScript(String script, WebDriver driver) {}

  @Override
  public void afterScript(String script, WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void beforeSwitchToWindow(String windowName, WebDriver driver) {}

  @Override
  public void afterSwitchToWindow(String windowName, WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public void onException(Throwable throwable, WebDriver driver) {
    appendWebDriverLog(driver);
  }

  @Override
  public <X> void beforeGetScreenshotAs(OutputType<X> target) {}

  @Override
  public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {}

  @Override
  public void beforeGetText(WebElement element, WebDriver driver) {}

  @Override
  public void afterGetText(WebElement element, WebDriver driver, String text) {
    appendWebDriverLog(driver);
  }
}
