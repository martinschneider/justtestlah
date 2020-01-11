package qa.justtestlah.galen.exception;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.JustTestLahConfiguration;
import qa.justtestlah.locator.LocatorMap;

/**
 * Galen specs include a list of UI elements under the @objects section. JustTestLah! already
 * maintains these locators in the YAML file of each page object. This class loads the UI locators
 * from a {@link LocatorMap} and creates the Galen spec file accordingly.
 */
@Component
public class UILocatorInjector {

  private static final Logger LOG = LoggerFactory.getLogger(UILocatorInjector.class);

  private static final String SPACES = "   ";

  @Autowired private JustTestLahConfiguration configuration;

  /**
   * @param templatePath path to the Galen template spec
   * @param locators the {@link LocatorMap}
   * @return path to the full specification (including the UI locators)
   */
  public String injectUILocators(String templatePath, LocatorMap locators) {
    File file = null;
    try {
      List<String> lines =
          formatUILocators(locators.getLocatorsForPlatform(configuration.getPlatform()));
      lines.addAll(Files.readAllLines(Paths.get(templatePath), StandardCharsets.UTF_8));
      file = File.createTempFile("galen-", "");
      Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      LOG.error("Error writing Galen spec", exception);
    }
    return file.getPath();
  }

  private List<String> formatUILocators(Map<String, Pair<String, String>> locatorMap) {
    List<String> lines = new ArrayList<>();
    lines.add("@objects");
    for (Map.Entry<String, Pair<String, String>> entry : locatorMap.entrySet()) {
      StringBuilder strBuilder = new StringBuilder(SPACES);
      strBuilder.append(entry.getKey());
      strBuilder.append(SPACES);
      strBuilder.append(entry.getValue().getLeft());
      strBuilder.append(SPACES);
      strBuilder.append(entry.getValue().getRight());
      lines.add(strBuilder.toString());
    }
    return lines;
  }
}
