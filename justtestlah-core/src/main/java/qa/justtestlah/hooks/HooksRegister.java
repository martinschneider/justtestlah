package qa.justtestlah.hooks;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.JustTestLahConfiguration;

/** Hooks are registered based on platform and execution environment. */
@Component
public class HooksRegister {
  private List<CucumberHook> registeredHooks = new ArrayList<CucumberHook>();

  @Autowired private JustTestLahConfiguration configuration;

  public void addHooks(CucumberHook hook) {
    if (hook.getPlatforms().contains(configuration.getPlatform())
        && hook.getExecutionEnvironments().contains(configuration.getExecutionEnvironment())) {
      registeredHooks.add(hook);
    }
  }

  public List<CucumberHook> getRegisteredHooks() {
    return registeredHooks;
  }
}
