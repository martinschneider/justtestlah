package io.github.martinschneider.yasew.junit;

import cucumber.api.CucumberOptions;
import cucumber.runtime.DefaultCucumberOptionsProvider;
import io.github.martinschneider.yasew.configuration.Platform;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionsProvider extends DefaultCucumberOptionsProvider {
  @SuppressWarnings("squid:S00116Field")
  private static final Logger LOG = LoggerFactory.getLogger(OptionsProvider.class);

  private static final String STEPS_PACKAGE_KEY = "steps.package";
  private static final String PLATFORM_KEY = "platform";
  private static final String FEATURES_DIRECTORY_KEY = "features.directory";
  private static final String DEFAULT_YASEW_PROPERTIES = "yasew.properties";
  private static final String YASEW_LOCATION_KEY = "yasew.properties";
  private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
  private static final String OPENCV_ENABLED_KEY = "opencv.enabled";
  private static final String CUCUMBER_REPORT_DIRECTORY_KEY = "cucumber.report.directory";
  private static final String DEFAULT_CUCUMBER_REPORT_DIRECTORY = "target/report/cucumber";
  private static final String DEFAULT_PLATFORM = "web";

  private Properties props;

  /** Constructor. */
  public OptionsProvider() {
    // Initialize Spring profiles and settings
    init();

    // load OpenCV library
    if (Boolean.parseBoolean(getProperty(OPENCV_ENABLED_KEY, "false"))) { // load the opencv library
      OpenCV.loadShared();
      OpenCV.loadLocally();
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
  }

  @Override
  public CucumberOptions getOptions() {
    Map<String, Object> optionsMap = new HashMap<String, Object>();
    optionsMap.put("tags", new String[] {"@" + getProperty(PLATFORM_KEY, DEFAULT_PLATFORM)});
    optionsMap.put(
        "glue",
        new String[] {"io.github.martinschneider.yasew.steps", getProperty(STEPS_PACKAGE_KEY)});
    optionsMap.put("features", new String[] {getProperty(FEATURES_DIRECTORY_KEY)});
    optionsMap.put("plugin", "pretty");
    optionsMap.put("plugin", "html:report");
    optionsMap.put(
        "plugin",
        "json:"
            + getProperty(CUCUMBER_REPORT_DIRECTORY_KEY, DEFAULT_CUCUMBER_REPORT_DIRECTORY)
            + "/cucumber.json");
    return getCucumberOptions(optionsMap);
  }

  private String getProperty(String key, String defaultValue) {
    initProperties();
    String value = props.getProperty(key);
    if (value != null && !value.isEmpty()) {
      LOG.debug("Reading property {} = {}", key, value);
      return value;
    }
    LOG.warn("Property {} not set in yasew.properties. Using default value: {}", key, defaultValue);
    return defaultValue;
  }

  private String getProperty(String key) {
    initProperties();
    String value = props.getProperty(key);
    if (value != null && !value.isEmpty()) {
      LOG.info("Reading property {} = {}", key, value);
      return value;
    }
    throw new RuntimeException("Mandatory property " + key + " not set in yasew.properties.");
  }

  private void initProperties() {
    if (props == null) {
      props = new Properties();
      loadProperties();
    }
  }

  private void loadProperties() {

    String propertiesLocation = System.getProperty(YASEW_LOCATION_KEY);
    try {
      if (propertiesLocation != null) {
        LOG.info("Loading Yasew properties from {}", propertiesLocation);
        props.load(new FileInputStream(propertiesLocation));
      } else {
        propertiesLocation = DEFAULT_YASEW_PROPERTIES;
        LOG.info("Loading Yasew properties from classpath ({})", propertiesLocation);
        props.load(YasewTest.class.getClassLoader().getResourceAsStream(propertiesLocation));
      }
    } catch (NullPointerException | IOException e) {
      LOG.warn("Error loading settings from {}", propertiesLocation);
    }
  }

  private void init() {
    // set the active Spring profile to the current platform
    String platform = getProperty(PLATFORM_KEY);
    if (platform == null || platform.isEmpty()) {
      LOG.info("No platform specified. Using default ({})", Platform.DEFAULT);
      platform = Platform.DEFAULT;
      System.setProperty(PLATFORM_KEY, platform);
    }
    String[] platforms = platform.split(",");
    if (platforms.length > 1) {
      throw new UnsupportedOperationException(
          "Please specify exactly one spring profile (ANDROID, IOS or WEB).");
    }
    platform = platforms[0].trim();
    String springProfiles = System.getProperty(SPRING_PROFILES_ACTIVE);
    if (springProfiles != null && !springProfiles.isEmpty()) {
      springProfiles += "," + platform;
    } else {
      springProfiles = platform;
    }
    LOG.info("Setting platform to {}", platform);
    System.setProperty(SPRING_PROFILES_ACTIVE, springProfiles);
  }
}
