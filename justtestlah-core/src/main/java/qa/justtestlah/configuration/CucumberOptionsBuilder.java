package qa.justtestlah.configuration;

import io.cucumber.core.options.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.exception.JustTestLahException;
import qa.justtestlah.log.CucumberLoggingPlugin;

/** Builds Cucumber options based on `justtestlah.properties` and sets them as System properties. */
public class CucumberOptionsBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(CucumberOptionsBuilder.class);

  private static final String PLATFORM_KEY = "platform";
  private static final String FEATURES_DIRECTORY_KEY = "features.directory";
  private static final String CUCUMBER_REPORT_DIRECTORY_KEY = "cucumber.report.directory";
  private static final String JUSTTESTLAH_SPRING_CONTEXT_KEY = "justtestlah.use.springcontext";
  private static final String DEFAULT_CUCUMBER_REPORT_DIRECTORY = "target/report/cucumber";
  private static final String DEFAULT_PLATFORM = "web";
  private static final String DELIMITER = ",";
  private static final String TAGS_KEY = "tags";
  private static final String STEPS_PACKAGE_KEY = "steps.package";

  public static void setCucumberOptions(PropertiesHolder properties) {
    setCucumberProperty(
        Constants.FEATURES_PROPERTY_NAME, properties.getProperty(FEATURES_DIRECTORY_KEY));
    setCucumberProperty(Constants.FILTER_TAGS_PROPERTY_NAME, buildTagsProperty(properties));
    setCucumberProperty(Constants.GLUE_PROPERTY_NAME, buildGlueProperty(properties));
    setCucumberProperty(Constants.PLUGIN_PROPERTY_NAME, buildPluginProperty(properties));
    setCucumberProperty(Constants.EXECUTION_STRICT_PROPERTY_NAME, "true");
  }

  private static void setCucumberProperty(String key, String value) {
    LOG.info("Setting {} = {}", key, value);
    System.setProperty(key, value);
  }

  private static String buildPluginProperty(PropertiesHolder properties) {
    StringBuilder pluginProperty = new StringBuilder(CucumberLoggingPlugin.class.getName());
    pluginProperty.append(DELIMITER);
    pluginProperty.append("html:report");
    pluginProperty.append(DELIMITER);
    pluginProperty.append("json:");
    pluginProperty.append(
        properties.getProperty(CUCUMBER_REPORT_DIRECTORY_KEY, DEFAULT_CUCUMBER_REPORT_DIRECTORY));
    pluginProperty.append("/cucumber.json");
    return pluginProperty.toString();
  }

  private static String buildTagsProperty(PropertiesHolder properties) {
    StringBuilder tagsBuilder = new StringBuilder("@");
    tagsBuilder.append(properties.getProperty(PLATFORM_KEY, DEFAULT_PLATFORM));
    String tags = properties.getProperty(TAGS_KEY, null);
    if (tags != null) {
      // Prevent injection attacks
      if (tags.contains("'")) {
        throw new JustTestLahException(
            String.format("Invalid character ' in tag expression: %s", tags));
      }
      // support legacy format (i.e. comma-separated list of tags without @)
      if (!tags.contains("@")) {
        for (String tag : tags.split(DELIMITER)) {
          tagsBuilder.append(" and @");
          tagsBuilder.append(tag);
        }
      } else // no format (tag expressions)
      {
        tagsBuilder.append(" and (");
        tagsBuilder.append(tags);
        tagsBuilder.append(")");
      }
    }
    return tagsBuilder.toString();
  }

  private static String buildGlueProperty(PropertiesHolder properties) {
    StringBuilder glueProperty = new StringBuilder();
    if (Boolean.parseBoolean(
        properties.getProperty(JUSTTESTLAH_SPRING_CONTEXT_KEY, Boolean.toString(true)))) {
      glueProperty.append("qa.justtestlah.steps");
    }
    glueProperty.append(",");
    glueProperty.append(properties.getProperty(STEPS_PACKAGE_KEY));
    return glueProperty.toString();
  }
}
