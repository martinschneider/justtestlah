package qa.justtestlah.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.yaml.snakeyaml.Yaml;
import qa.justtestlah.aop.AopConfig;
import qa.justtestlah.locator.LocatorPlaceholders;

/** JustTestLah! Spring context. */
@Configuration
@PropertySource(
    value = {"file:${justtestlah.properties}", "justtestlah.properties"},
    ignoreResourceNotFound = true)
@ComponentScan(basePackages = {"qa.justtestlah", "${pages.package}", "${steps.package}"})
@Import(AopConfig.class)
public class JustTestLahSpringConfig {

  @Value("${pages.package}")
  private String pagesPackage;

  @Value("${locator.placeholders.file}")
  private String locatorPlaceholdersFile;

  @Bean
  public Yaml yamlParser() {
    return new Yaml();
  }

  @Bean
  public LocatorPlaceholders globalProperties() {
    return new LocatorPlaceholders(pagesPackage, locatorPlaceholdersFile);
  }
}
