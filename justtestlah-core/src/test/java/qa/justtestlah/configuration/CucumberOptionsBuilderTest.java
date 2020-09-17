package qa.justtestlah.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import qa.justtestlah.exception.JustTestLahException;
import qa.justtestlah.log.CucumberLoggingPlugin;

public class CucumberOptionsBuilderTest {

  @Test
  public void testLegacyTags() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah_legacy.properties").getFile());
    CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
    assertThat(System.getProperty("cucumber.filter.tags"))
        .as("check cucumber filters")
        .isEqualTo("@web and @regression");
  }

  @Test
  public void testTags() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah.properties").getFile());
    CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
    assertThat(System.getProperty("cucumber.filter.tags"))
        .as("check cucumber filters")
        .isEqualTo("@web and (@regression or not @skip)");
  }

  @Test
  public void testInvalidTags() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah_injection.properties").getFile());
    Throwable exception =
        assertThrows(
            JustTestLahException.class,
            () -> {
              CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
            });
    assertThat(exception.getMessage())
        .as("check exception message")
        .isEqualTo("Invalid character ' in tag expression: ' -- do something nasty ;-)");
  }

  @Test
  public void testGlue() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah.properties").getFile());
    CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
    assertThat(System.getProperty("cucumber.glue"))
        .as("check cucumber glue")
        .isEqualTo("qa.justtestlah.steps,com.test");
  }

  @Test
  public void testFeatures() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah.properties").getFile());
    CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
    assertThat(System.getProperty("cucumber.features"))
        .as("check cucumber features")
        .isEqualTo("src/test/resources/features");
  }

  @Test
  public void testPlugin() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah.properties").getFile());
    CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
    assertThat(System.getProperty("cucumber.plugin"))
        .as("check cucumber plugin")
        .isEqualTo(CucumberLoggingPlugin.class.getName());
  }

  @Test
  public void testStrict() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah.properties").getFile());
    CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
    assertThat(System.getProperty("cucumber.execution.strict"))
        .as("check cucumber strict")
        .isEqualTo("true");
  }
}
