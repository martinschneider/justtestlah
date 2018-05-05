package io.github.martinschneider.yasew.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

public class BaseTest {

  private TestComponentParent target = new TestComponentParent();

  @Mock private ApplicationContext applicationContext;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    // mocking the Spring application context
    doReturn(new TestPageObject()).when(applicationContext).getBean(any(Class.class));
  }

  /**
   * Test the injection of page objects 
   */
  @Test
  public void testInjection() {
    target.setApplicationContext(applicationContext);
    target.initPages();
    assertThat(target.getPage()).as("check page object injection").isNotNull();
  }

  private class TestComponentParent extends Base {
    private TestPageObject page;

    public TestPageObject getPage() {
      return page;
    }
  }

  private class TestPageObject extends BasePage<TestPageObject> {}
}
