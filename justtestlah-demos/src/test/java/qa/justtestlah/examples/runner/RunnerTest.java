package qa.justtestlah.examples.runner;

import org.junit.Before;
import qa.justtestlah.configuration.PropertiesHolder;
import qa.justtestlah.junit.JustTestLahTest;

public class RunnerTest extends JustTestLahTest {
  @Before
  public void cleanUp() {
    System.setProperty(PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY, "");
  }
}
