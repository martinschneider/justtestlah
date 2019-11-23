package qa.justtestlah.configuration;

import com.applitools.eyes.selenium.Eyes;
import com.galenframework.reports.GalenTestInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.yaml.snakeyaml.Yaml;
import qa.justtestlah.aop.AopConfig;
import qa.justtestlah.locator.LocatorParser;
import qa.justtestlah.locator.LocatorPlaceholders;
import qa.justtestlah.testdata.TestDataMap;
import qa.justtestlah.testdata.TestDataObjectRegistry;
import qa.justtestlah.testdata.TestDataParser;
import qa.justtestlah.visual.AppiumTemplateMatcher;
import qa.justtestlah.visual.ImageUtils;
import qa.justtestlah.visual.OpenCVTemplateMatcher;
import qa.justtestlah.visual.TemplateMatcher;

/** JustTestLah! Spring context. */
@Configuration
@PropertySource(
    value = {"file:${justtestlah.properties}", "justtestlah.properties"},
    ignoreResourceNotFound = true)
@ComponentScan(basePackages = {"qa.justtestlah.hooks", "${pages.package}", "${steps.package}"})
@Import(AopConfig.class)
public class SpringConfig {

  private static final String BROWSER_STACK_WEB_DRIVER_BUILDER_CLASS =
      "qa.justtestlah.configuration.BrowserStackWebDriverBuilder";

  private static final Logger LOG = LoggerFactory.getLogger(SpringConfig.class);

  @Value("${opencv.mode}")
  private String openCVmode;

  @Value("${eyes.apiKey}")
  private String eyesApiKey;

  @Value("${cloudprovider:local}")
  private String cloudProvider;

  @Value("${pages.package}")
  private String pagesPackage;

  @Value("${locator.placeholders.file}")
  private String locatorPlaceholdersFile;

  private List<GalenTestInfo> galenTests = new LinkedList<>();

  @Bean
  public JustTestLahConfiguration config() {
    return new JustTestLahConfiguration(webDriverBuilder());
  }

  @Bean
  public Yaml yamlParser() {
    return new Yaml();
  }

  @Bean
  public LocatorParser locatorParser() {
    return new LocatorParser();
  }

  @Bean
  public LocatorPlaceholders globalProperties() {
    return new LocatorPlaceholders(pagesPackage, locatorPlaceholdersFile);
  }

  /**
   * Construct the matching {@link WebDriverBuilder}.
   *
   * @return {@link WebDriverBuilder} matching the configured cloud provider
   */
  @Bean
  public WebDriverBuilder webDriverBuilder() {
    if (cloudProvider.equals("local")) {
      return new LocalWebDriverBuilder();
    }
    // TODO: use Spring to contribute WebDriverBuilders from other modules instead
    // of hard-coding
    // the class names
    else if (cloudProvider.equals("browserstack")) {
      try {
        return getWebDriverBuilder(BROWSER_STACK_WEB_DRIVER_BUILDER_CLASS);
      } catch (Exception exception) {
        LOG.error(
            "Couldn't instantiate BrowserStackWebDriverBuilder. Ensure that you included `justestlah-browserstack` in your pom.xml!");
        System.exit(1);
      }
    }
    return null;
  }

  private WebDriverBuilder getWebDriverBuilder(String className)
      throws InstantiationException, IllegalAccessException, InvocationTargetException,
          NoSuchMethodException, ClassNotFoundException {
    return (WebDriverBuilder) Class.forName(className).getConstructor().newInstance();
  }

  @Bean
  public ImageUtils imageUtils() {
    return new ImageUtils();
  }

  @Bean
  public TemplateMatcher templateMatcher() {
    ImageUtils imageUtils = new ImageUtils();
    if (openCVmode.equals("server")) {
      return new AppiumTemplateMatcher(imageUtils);
    } else {
      return new OpenCVTemplateMatcher(imageUtils);
    }
  }

  @Bean
  public TestDataMap testDataMap() {
    return new TestDataMap();
  }

  @Bean
  public TestDataParser testDataParser() {
    return new TestDataParser();
  }

  @Bean
  public TestDataObjectRegistry testDataObjectRegistry() {
    return new TestDataObjectRegistry();
  }

  /**
   * Applitools.
   *
   * @return Applitools {@link Eyes}
   */
  @Bean(destroyMethod = "")
  /**
   * Spring would call close() otherwise which will throw an Exception because we already close it
   * ourselves.
   */
  public Eyes eyes() {
    Eyes eyes = new Eyes();
    eyes.setApiKey(eyesApiKey);
    return eyes;
  }

  @Bean
  public List<GalenTestInfo> galenTests() {
    return galenTests;
  }
}
