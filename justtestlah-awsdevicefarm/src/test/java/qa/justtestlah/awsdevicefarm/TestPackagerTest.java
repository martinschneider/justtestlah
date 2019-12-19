package qa.justtestlah.awsdevicefarm;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.configuration.PropertiesHolder;

public class TestPackagerTest {

  private Logger LOG = LoggerFactory.getLogger(TestPackagerTest.class);

  private TestPackager target;

  @Mock
  private PropertiesHolder properties;

  @Test
  public void testMavenPackaging() throws MavenInvocationException, MalformedURLException,
      IOException, ReflectiveOperationException {

//    String tmpPath = System.getProperty("java.io.tmpdir");
    String currentPath = Paths.get("").toFile().getAbsolutePath();
//
//    LOG.info("Downloading Maven");
//    File mavenZip = new File(
//        System.getProperty("java.io.tmpdir") + File.separator + "apache-maven-3.6.3-bin.zip");
//    FileUtils.copyURLToFile(new URL(
//        "https://www-us.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip"),
//        mavenZip, 3000, 3000);
//    LOG.info("Maven downloaded to {}", mavenZip.getAbsolutePath());
//
//    LOG.info("Unzipping Maven package");
//    ZipFile zipFile = new ZipFile(mavenZip.getAbsolutePath());
//    zipFile.extractAll(mavenZip.getParent());
//
//    System.setProperty("maven.home", tmpPath + File.separator + "apache-maven-3.6.3");
    updateEnv("JAVA_HOME", System.getProperty("java.home"));

    MockitoAnnotations.initMocks(this);
    when(properties.getProperty("aws.demo.path")).thenReturn(currentPath);
    when(properties.getProperty("aws.testpackage.name")).thenReturn("justtestlah-awsdevicefarm");

    target = new TestPackager(properties);

    LOG.info("AWS Test package created: {}", target.packageProjectForDeviceFarm(false));
    assertThat(Files.exists(Paths.get(currentPath + File.separator + "target" + File.separator
        + "justtestlah-awsdevicefarm.zip"))).isTrue();
    
  }

  // helper method to set an environment variable
  private void updateEnv(String name, String val) throws ReflectiveOperationException {
    Map<String, String> env = System.getenv();
    Field field = env.getClass().getDeclaredField("m");
    field.setAccessible(true);
    ((Map<String, String>) field.get(env)).put(name, val);
  }
}
