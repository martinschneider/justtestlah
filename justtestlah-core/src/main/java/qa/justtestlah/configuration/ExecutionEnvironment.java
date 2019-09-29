package qa.justtestlah.configuration;

/** Enum for execution environments. */
public enum ExecutionEnvironment {
  LOCAL("local"),
  BROWSERSTACK("browserstack"),
  AWS_DEVICEFARM("awsdevicefarm"),
  DEFAULT("local");

  private String executionEnvironmentName;

  ExecutionEnvironment(String executionEnvironmentName) {
    this.executionEnvironmentName = executionEnvironmentName;
  }

  public String getExecutionEnvironmentName() {
    return executionEnvironmentName;
  }
}
