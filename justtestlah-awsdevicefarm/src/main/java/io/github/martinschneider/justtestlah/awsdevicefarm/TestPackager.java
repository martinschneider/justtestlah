package io.github.martinschneider.justtestlah.awsdevicefarm;

import io.github.martinschneider.justtestlah.configuration.PropertiesHolder;
import java.io.File;
import java.util.List;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;

/** Executes a Maven build to create a test package for AWS Devicefarm. */
public class TestPackager {

  private PropertiesHolder properties;

  public TestPackager(PropertiesHolder properties) {
    this.properties = properties;
  }

  public File packageProjectForDeviceFarm() throws MavenInvocationException {
    InvocationRequest request = new DefaultInvocationRequest();
    request.setPomFile(
        new File(properties.getProperty("aws.demo.path") + File.separator + "pom.xml"));
    request.setProfiles(List.of("aws"));
    request.setGoals(List.of("clean", "package"));
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
