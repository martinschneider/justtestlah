package io.github.martinschneider.justtestlah.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

public class JustTestLahRunnerTest {

  private JustTestLahRunner target;

  @Test
  public void testBuildCucumberOptions() throws InitializationError, IOException {
    System.setProperty(
        JustTestLahRunner.JUST_TEST_LAH_LOCATION_KEY,
        JustTestLahRunnerTest.class.getResource("justtestlah.properties").getFile());
    target = new JustTestLahRunner(this.getClass());
    assertThat(target.buildCucumberOptions())
        .as("check cucumber options")
        .isEqualTo(
            "--tags @web --tags @regression --glue io.github.martinschneider.justtestlah.steps  --glue com.test --plugin pretty --plugin html:report --plugin json:target/report/cucumber/cucumber.json src/test/resources/features");
  }
}
