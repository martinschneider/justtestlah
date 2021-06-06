package qa.justtestlah.log;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepStarted;
import qa.justtestlah.utils.SpringContext;

/** Cucumber plugin to write scenario- and step-based log messages. */
public class CucumberLoggingPlugin implements ConcurrentEventListener {

  private EventHandler<TestCaseStarted> caseStartedHandler =
      new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
          TestCase testCase = event.getTestCase();
          SpringContext.getBean(TestLogWriter.class)
              .log(
                  LogLevel.INFO,
                  TestLogWriter.CUCUMBER_SCENARIO_INDENTATION,
                  "Scenario: {} ({}:{})",
                  testCase.getName(),
                  testCase.getUri(),
                  // TODO: getLine() has been deprecated. Once this gets removed from Cucumber, we
                  // will
                  // simply drop it from the log too
                  testCase.getLine());
        }
      };

  private EventHandler<TestStepStarted> stepStartedHandler =
      new EventHandler<TestStepStarted>() {
        @Override
        public void receive(TestStepStarted event) {
          TestStep step = event.getTestStep();
          if (step instanceof PickleStepTestStep) {
            Step pickle = ((PickleStepTestStep) step).getStep();
            SpringContext.getBean(TestLogWriter.class)
                .log(
                    LogLevel.INFO,
                    TestLogWriter.CUCUMBER_STEP_INDENTATION,
                    "Step: {}",
                    pickle.getText(),
                    pickle.getLine());
          }
        }
      };

  private EventHandler<TestCaseFinished> caseFinishedHandler =
      new EventHandler<TestCaseFinished>() {
        @Override
        public void receive(TestCaseFinished event) {
          Result result = event.getResult();
          Throwable error = result.getError();
          if (error == null) {
            SpringContext.getBean(TestLogWriter.class)
                .log(
                    LogLevel.INFO,
                    TestLogWriter.CUCUMBER_SCENARIO_INDENTATION,
                    "[{}] Scenario \"{}\" finished after {} seconds\n",
                    result.getStatus(),
                    event.getTestCase().getName(),
                    result.getDuration().toSeconds());
          } else {
            String errorMsg = error.getMessage();
            if (errorMsg != null) {
              errorMsg = errorMsg.replaceAll("[\\t\\n\\r]+", " ");
            }
            SpringContext.getBean(TestLogWriter.class)
                .log(
                    LogLevel.INFO,
                    TestLogWriter.CUCUMBER_SCENARIO_INDENTATION,
                    "[{}] Scenario \"{}\" finished after {} seconds with error \"{}\"\n",
                    result.getStatus(),
                    event.getTestCase().getName(),
                    result.getDuration().toSeconds(),
                    errorMsg);
          }
        }
      };

  @Override
  public void setEventPublisher(EventPublisher publisher) {
    publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
    publisher.registerHandlerFor(TestStepStarted.class, stepStartedHandler);
    publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedHandler);
  }
}
