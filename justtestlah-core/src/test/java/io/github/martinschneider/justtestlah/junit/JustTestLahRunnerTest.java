package io.github.martinschneider.justtestlah.junit;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.github.martinschneider.justtestlah.configuration.PropertiesHolder;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

@RunWith(DataProviderRunner.class)
public class JustTestLahRunnerTest {

  private JustTestLahRunner target;

  @Test
  @UseDataProvider("testData")
  public void testBuildCucumberOptions(String propertiesFile, String expectedCucumberOptions)
      throws InitializationError, IOException {
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        JustTestLahRunnerTest.class.getResource(propertiesFile).getFile());
    target = new JustTestLahRunner(this.getClass());
    assertThat(target.buildCucumberOptions())
        .as("check cucumber options")
        .isEqualTo(expectedCucumberOptions);
  }

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testInvalidCharactersInCucumberOptions() throws InitializationError, IOException {
    exceptionRule.expect(RuntimeException.class);
    exceptionRule.expectMessage("Invalid character ' in tag expression: '");
    System.setProperty(
        PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY,
        JustTestLahRunnerTest.class.getResource("justtestlah_injection.properties").getFile());
    target = new JustTestLahRunner(this.getClass());
    assertThat(target.buildCucumberOptions()).as("check cucumber options");
  }

  @DataProvider
  public static Object[][] testData() {
    return new Object[][] {
      {
        "justtestlah_legacy.properties",
        "--tags '@web and @regression' --glue io.github.martinschneider.justtestlah.steps  --glue com.test --plugin pretty --plugin html:report --plugin json:target/report/cucumber/cucumber.json src/test/resources/features --strict"
      },
      {
        "justtestlah_tagexpression.properties",
        "--tags '@web and (@regression or not @skip)' --glue io.github.martinschneider.justtestlah.steps  --glue com.test --plugin pretty --plugin html:report --plugin json:target/report/cucumber/cucumber.json src/test/resources/features --strict"
      }
    };
  }
}
