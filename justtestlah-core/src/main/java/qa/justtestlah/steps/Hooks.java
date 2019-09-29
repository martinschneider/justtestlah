package qa.justtestlah.steps;

import io.cucumber.core.api.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import qa.justtestlah.configuration.SpringConfig;
import qa.justtestlah.hooks.ApplitoolsHooks;
import qa.justtestlah.hooks.CucumberHook;
import qa.justtestlah.hooks.GalenHooks;
import qa.justtestlah.hooks.HooksRegister;
import qa.justtestlah.hooks.OpenCVHooks;
import qa.justtestlah.hooks.WebDriverHooks;

/** This class registers Cucumber hooks with {@link HooksRegister}. */
@ContextConfiguration(classes = SpringConfig.class)
public class Hooks {

  @Autowired private HooksRegister hooksRegister;

  @Autowired private WebDriverHooks webDriver;

  @Autowired private ApplitoolsHooks applitools;

  @Autowired private GalenHooks galen;

  @Autowired private OpenCVHooks openCV;

  private void initHooks() {
    hooksRegister.addHooks(webDriver);
    hooksRegister.addHooks(openCV);
    hooksRegister.addHooks(galen);
    hooksRegister.addHooks(applitools);
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
  }
}
