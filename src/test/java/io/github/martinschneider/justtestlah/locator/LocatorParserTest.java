package io.github.martinschneider.justtestlah.locator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class LocatorParserTest {
  private LocatorParser target = new LocatorParser();

  @Test
  public void test() throws IOException {

    target.setYamlParser(new Yaml());
    String baseFolder = this.getClass().getPackage().getName().replaceAll("\\.", "/");
    Map<String, Map<String, Map<String, String>>> locatorMap =
        target.parse(baseFolder + "/LocatorParserTest.yaml");

    assertThat(locatorMap.get("SEARCH_FIELD")).as("verifying SEARCH_FIELD locator").isNotNull();
    assertThat(locatorMap.get("SEARCH_FIELD").get("android"))
        .as("verifying SEARCH_FIELD Android locator")
        .isNotNull();
    assertThat(locatorMap.get("SEARCH_FIELD").get("android").get("type"))
        .as("verifying SEARCH_FIELD Android locator type")
        .isNotNull()
        .isEqualTo("id");
    assertThat(locatorMap.get("SEARCH_FIELD").get("android").get("value"))
        .as("verifying SEARCH_FIELD Android locator type")
        .isNotNull()
        .isEqualTo("com.stackexchange.stackoverflow:id/search_src_text");
  }
}
