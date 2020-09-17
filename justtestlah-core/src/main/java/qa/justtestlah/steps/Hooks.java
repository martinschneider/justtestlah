package qa.justtestlah.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import qa.justtestlah.configuration.JustTestLahSpringConfig;
import qa.justtestlah.hooks.CucumberHook;
import qa.justtestlah.hooks.HooksRegister;
import qa.justtestlah.hooks.WebDriverHooks;

/** This class registers Cucumber hooks with {@link HooksRegister}. */
@ContextConfiguration(classes = JustTestLahSpringConfig.class)
@CucumberContextConfiguration
public class Hooks {

  @Autowired private HooksRegister hooksRegister;

  @Autowired private WebDriverHooks webDriver;

  private void initHooks() {
    hooksRegister.addHooks(webDriver);
  }

  @Before
  public void notifyBefore(Scenario scenario) {
    initHooks();
    for (CucumberHook hook : hooksRegister.getRegisteredHooks()) {
      hook.before(scenario);
    }
  }

  @After
  public void notifyAfter(Scenario scenario) {
    for (CucumberHook hook : hooksRegister.getRegisteredHooks()) {
      hook.after(scenario);
    }
    hooksRegister.clear();
  }
}
