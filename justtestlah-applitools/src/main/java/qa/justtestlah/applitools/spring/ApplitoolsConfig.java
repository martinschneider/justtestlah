package qa.justtestlah.applitools.spring;

import com.applitools.eyes.selenium.Eyes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** JustTestLah! Applitools Spring context. */
@Configuration
public class ApplitoolsConfig {

  @Value("${eyes.apiKey}")
  private String eyesApiKey;

  @Bean(destroyMethod = "")
  public Eyes eyes() {
    Eyes eyes = new Eyes();
    eyes.setApiKey(eyesApiKey);
    return eyes;
  }
}
