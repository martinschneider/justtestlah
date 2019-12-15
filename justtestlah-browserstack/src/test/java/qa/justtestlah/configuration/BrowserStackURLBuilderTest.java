package qa.justtestlah.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import qa.justtestlah.exception.BrowserstackException;

/** Test for {@link BrowserStackUrlBuilder}. */
public class BrowserStackURLBuilderTest {

  private BrowserStackUrlBuilder target = new BrowserStackUrlBuilder();

  @Test
  public void testBuildBrowserStackUrl() {
    assertThat(target.buildBrowserStackUrl("access", "user").toString())
        .isEqualTo("http://user:access@hub-cloud.browserstack.com/wd/hub");
  }

  @Test(expected = BrowserstackException.class)
  public void testInvalidUrl() {
    target.buildBrowserStackUrl(":/", "");
  }
}
