package qa.justtestlah.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

public class BaseTest {

  private TestComponentParent target = new TestComponentParent();

  @Mock private ApplicationContext applicationContext;

  private AutoCloseable mocks;

  /** Initialise mocks. */
  @Before
  @SuppressWarnings("unchecked")
  public void init() {
    mocks = MockitoAnnotations.openMocks(this);
    // mocking the Spring application context
    doReturn(new TestPageObject()).when(applicationContext).getBean(any(Class.class));
  }

  @After
  public void finish() throws Exception {
    mocks.close();
  }

  /** Test the injection of page objects . */
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
