package qa.justtestlah.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import qa.justtestlah.exception.JustTestLahException;

public class CucumberOptionsBuilderTest {

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

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

  @Test(expected = JustTestLahException.class)
  public void testInvalidTags() {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        CucumberOptionsBuilder.class.getResource("justtestlah_injection.properties").getFile());
    CucumberOptionsBuilder.setCucumberOptions(new PropertiesHolder());
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
        .isEqualTo("pretty,html:report,json:target/report/cucumber/cucumber.json");
  }
}
