package qa.justtestlah.locator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;

/**
 * Holds a {@link Properties} map with placeholders to be used inside the locator YAML files.
 *
 * <p>First, such placeholders are loaded from a file called `placeholder.properties` in the
 * `pagesPackage` folder.
 *
 * <p>If `locator.placeholders.file` is set in `justtestlah.properties` placeholders are also loaded
 * from the absolute path defined there.
 *
 * <p>In case a placeholder exists in both the latter will be used.
 */
@Component
public class LocatorPlaceholders {

  private static final String PLACEHOLDER_PROPERTIES_FILENAME = "placeholder.properties";

  private static final Logger LOG = LoggerFactory.getLogger(LocatorPlaceholders.class);

  private Properties placeholders;

  /**
   * Initialises {@link LocatorPlaceholders} from the following two sources:
   *
   * <ol>
   *   <li>placeholder.properties in the pages package
   *   <li>the file passed as locator.placeholders.file in justtestlah.properties
   * </ol>
   *
   * Placeholders present in both files will be used from the one passed in
   * locator.placeholders.file
   *
   * @param pagesPackage the pages package
   * @param locatorPlaceholdersFile path to locator properties file
   */
  public LocatorPlaceholders(String pagesPackage, String locatorPlaceholdersFile) {
    String path =
        pagesPackage.replaceAll("\\.", File.separator)
            + File.separator
            + PLACEHOLDER_PROPERTIES_FILENAME;
    LOG.info("Loading placeholders from {}", path);
    placeholders = loadProperties(BasePage.class.getClassLoader().getResourceAsStream(path));
    if (locatorPlaceholdersFile != null && !locatorPlaceholdersFile.isEmpty()) {
      LOG.info("Loading placeholders from {}", locatorPlaceholdersFile);
      try {
        placeholders.putAll(loadProperties(new FileInputStream(locatorPlaceholdersFile)));
      } catch (FileNotFoundException exception) {
        LOG.warn(
            "Could not load placeholders. The file {} does not exist.", locatorPlaceholdersFile);
      }
    }
  }

  private Properties loadProperties(InputStream input) {
    Properties props = new Properties();
    if (input == null) {
      LOG.warn("Could not load placeholders");
      return props;
    }
    try {
      props.load(input);
      input.close();
    } catch (IOException exception) {
      LOG.warn("Could not load placeholders");
    }
    return props;
  }

  public Properties getProps() {
    return placeholders;
  }
}
