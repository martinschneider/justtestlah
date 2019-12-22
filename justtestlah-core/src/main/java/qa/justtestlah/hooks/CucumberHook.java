package qa.justtestlah.hooks;

import io.cucumber.java.Scenario;
import java.util.Set;
import qa.justtestlah.configuration.ExecutionEnvironment;
import qa.justtestlah.configuration.Platform;

/** Interface for Cucumber before and after hooks. */
public interface CucumberHook {

  /**
   * Add steps that are to be executed before the start of a scenario.
   *
   * @param scenario Cucumber scenario
   */
  public void before(Scenario scenario);

  /**
   * Add steps that are to be executed after the end of a scenario.
   *
   * @param scenario Cucumber scenario
   */
  public void after(Scenario scenario);

  /** @return Set of platforms the Hooks are supported. */
  public Set<Platform> getPlatforms();

  /** @return Set of cloud providers the Hooks are supported. */
  public Set<ExecutionEnvironment> getExecutionEnvironments();
}
