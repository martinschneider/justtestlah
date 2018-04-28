package io.github.martinschneider.yasew.locator;

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Proxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.github.martinschneider.yasew.base.BasePage;
import io.github.martinschneider.yasew.configuration.YasewConfiguration;

@RunWith(DataProviderRunner.class)
public class LocatorTest {
  private static final String SELENIDE_ELEMENT_PROXY_CLASS =
      "com.codeborne.selenide.impl.SelenideElementProxy";

  private LocatorMap target;

  @Mock private YasewConfiguration configuration;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    TestPageObject pageObject = new TestPageObject();
    pageObject.setConfiguration(configuration);
    pageObject.initializeLocatorMap();
    target = pageObject.getLocators();
    assertThat(target.isEmpty()).as("check locator loading").isFalse();
    assertThat(target.size()).as("check number of locators").isEqualTo(6);
  }

  @DataProvider
  public static Object[][] locatorData() {
    return new Object[][] {
      {"XPATH_LOCATOR", "demoXpath"},
      {"CSS_LOCATOR", "demoCss"},
      {"CSS_DEFAULT_LOCATOR", "demoCssDefault"},
      {"ID_LOCATOR", "demoId"},
      {"ACCESIBILITY_ID_LOCATOR", "demoAccesibilityId"},
      {"UIAUTOMATOR_LOCATOR", "demoUiAutomator"},
    };
  }

  @Test
  @UseDataProvider("locatorData")
  public void testLocatorMap(String key, String value) {
    assertThat(
            Proxy.getInvocationHandler(target.getLocator(key))
                .getClass()
                .getName()
                .equals(SELENIDE_ELEMENT_PROXY_CLASS))
        .as("check xpath locator")
        .isTrue();
    // TODO: verify the locator details
  }

  @SuppressWarnings({"unchecked"})
  protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
    if (AopUtils.isJdkDynamicProxy(proxy)) {
      return (T) ((Advised) proxy).getTargetSource().getTarget();
    } else {
      return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
    }
  }

  public class TestPageObject extends BasePage<TestPageObject> {
    public LocatorMap getLocators() {
      return super.getLocators();
    }
  }
}
