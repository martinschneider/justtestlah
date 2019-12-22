package qa.justtestlah.hooks;

import java.util.Set;

import io.cucumber.java.Scenario;
import qa.justtestlah.configuration.ExecutionEnvironment;
import qa.justtestlah.configuration.Platform;

/** Default implementation of {@link CucumberHook}. */
public abstract class AbstractCucumberHook implements CucumberHook {

  @Override
  public void before(Scenario scenario) {
	  // do nothing
  }

  @Override
  public void after(Scenario scenario) {
	  // do nothing
  }

  @Override
  public Set<Platform> getPlatforms() {
    return Set.of(Platform.ANDROID, Platform.IOS, Platform.WEB);
  }

  @Override
  public Set<ExecutionEnvironment> getExecutionEnvironments() {
    return Set.of(
        ExecutionEnvironment.BROWSERSTACK,
        ExecutionEnvironment.AWS_DEVICEFARM,
        ExecutionEnvironment.LOCAL);
  }
}
