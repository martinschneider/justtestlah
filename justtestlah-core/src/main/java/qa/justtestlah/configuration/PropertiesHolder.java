package qa.justtestlah.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.exception.JustTestLahException;
import qa.justtestlah.junit.JustTestLahTest;

/** Loads and manages the test configuration from `justtestlah.properties` */
public class PropertiesHolder {
  private static final Logger LOG = LoggerFactory.getLogger(PropertiesHolder.class);
  public static final String DEFAULT_JUST_TEST_LAH_PROPERTIES = "justtestlah.properties";
  public static final String JUST_TEST_LAH_LOCATION_KEY = "justtestlah.properties";
  private static final String EXPOSE_SYSTEM_PROPERTIES_KEY = "expose.system.properties";

  private Properties props;

  public PropertiesHolder() {
    props = new Properties();
    loadProperties();
  }

  public String getProperty(String key, String defaultValue) {
    String value = props.getProperty(key);
    if (value != null && !value.isEmpty()) {
      LOG.debug("Reading property {} = {}", key, value);
      return value;
    }
    LOG.warn(
        "Property {} not set in justtestlah.properties. Using default value: {}",
        key,
        defaultValue);
    return defaultValue;
  }

  public String getProperty(String key) {
    String value = props.getProperty(key);
    if (value != null && !value.isEmpty()) {
      LOG.info("Reading property {} = {}", key, value);
      return value;
    }
    throw new JustTestLahException(
        "Mandatory property " + key + " not set in justtestlah.properties.");
  }

  public String getOptionalProperty(String key) {
    return props.getProperty(key);
  }

  private void loadProperties() {
    String propertiesLocation = System.getProperty(JUST_TEST_LAH_LOCATION_KEY);
    try {
      if (propertiesLocation != null && !propertiesLocation.isEmpty()) {
        LOG.info("Loading JustTestLah properties from {}", propertiesLocation);
        props.load(new FileInputStream(propertiesLocation));
      } else {
        propertiesLocation = DEFAULT_JUST_TEST_LAH_PROPERTIES;
        LOG.info("Loading JustTestLah properties from classpath ({})", propertiesLocation);
        props.load(JustTestLahTest.class.getClassLoader().getResourceAsStream(propertiesLocation));
      }
      if (props.get(EXPOSE_SYSTEM_PROPERTIES_KEY) == null
          || Boolean.parseBoolean(props.get(EXPOSE_SYSTEM_PROPERTIES_KEY).toString())) {
        props.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
      }
    } catch (NullPointerException | IOException exception) {
      LOG.warn("Error loading settings from {}", propertiesLocation);
    }
    props.forEach((key, value) -> LOG.debug("{}={}", key, value));
  }

  public Properties getProperties() {
    return props;
  }
}
