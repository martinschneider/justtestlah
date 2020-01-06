package qa.justtestlah.locator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import qa.justtestlah.configuration.Platform;

public class LocatorParserTest {
  private LocatorParser target = new LocatorParser();

  @Test
  public void testLocatorParser() throws IOException {

    target.setYamlParser(new Yaml());
    String baseFolder = this.getClass().getPackage().getName().replace(".", File.separator);
    Map<String, Map<String, Map<String, String>>> locatorMap =
        target.parse(baseFolder + File.separator + "LocatorParserTest.yaml");

    assertThat(locatorMap.get("SEARCH_FIELD")).as("verifying SEARCH_FIELD locator").isNotNull();
    assertThat(locatorMap.get("SEARCH_FIELD").get(Platform.ANDROID.getPlatformName()))
        .as("verifying SEARCH_FIELD Android locator")
        .isNotNull();
    assertThat(locatorMap.get("SEARCH_FIELD").get(Platform.ANDROID.getPlatformName()).get("type"))
        .as("verifying SEARCH_FIELD Android locator type")
        .isNotNull()
        .isEqualTo("id");
    assertThat(locatorMap.get("SEARCH_FIELD").get(Platform.ANDROID.getPlatformName()).get("value"))
        .as("verifying SEARCH_FIELD Android locator type")
        .isNotNull()
        .isEqualTo("com.stackexchange.stackoverflow:id/search_src_text");
  }
}
