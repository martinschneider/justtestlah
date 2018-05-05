package io.github.martinschneider.yasew.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.github.martinschneider.yasew.configuration.YasewConfiguration;
import io.github.martinschneider.yasew.locator.LocatorMap;

public class BasePageTest {

  private TestPageObject target = new TestPageObject();

  @Mock private YasewConfiguration configuration;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testLocatorMap() {
    target.setConfiguration(configuration);
    target.initializeLocatorMap();
    LocatorMap locators = target.getLocators();
    assertThat(locators.isEmpty()).as("check locator loading").isFalse();
    assertThat(locators.size()).as("check number of locators").isEqualTo(3);
  }

  public class TestPageObject extends BasePage<TestPageObject> {
  }
}
