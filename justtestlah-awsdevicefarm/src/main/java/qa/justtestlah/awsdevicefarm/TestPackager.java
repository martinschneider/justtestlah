package qa.justtestlah.awsdevicefarm;

import java.io.File;
import java.util.List;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;
import qa.justtestlah.configuration.PropertiesHolder;

/** Executes a Maven build to create a test package for AWS Devicefarm. */
public class TestPackager {

  private PropertiesHolder properties;

  public TestPackager(PropertiesHolder properties) {
    this.properties = properties;
  }

  /**
   * Creates a test package for AWS Devicefarm.
   *
   * @param clean true, if the target directory should be cleaned (forcing recompilation)
   * @return {@link File} of the ZIP package to be used by AWS Devicefarm
   * @throws MavenInvocationException
   */
  public File packageProjectForDeviceFarm(boolean clean) throws MavenInvocationException {
    InvocationRequest request = new DefaultInvocationRequest();
    request.setPomFile(
        new File(properties.getProperty("aws.demo.path") + File.separator + "pom.xml"));
    request.setProfiles(List.of("aws"));
    if (clean) {
      request.setGoals(List.of("clean", "package"));
    } else {
      request.setGoals(List.of("package"));
    }
    request.setUpdateSnapshots(true);
    new DefaultInvoker().execute(request);
    return new File(
        properties.getProperty("aws.demo.path")
            + File.separator
            + "target"
            + File.separator
            + properties.getProperty("aws.testpackage.name")
            + ".zip");
  }
}
