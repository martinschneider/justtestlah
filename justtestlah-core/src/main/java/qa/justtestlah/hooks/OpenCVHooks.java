package qa.justtestlah.hooks;

import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.cucumber.java.Scenario;
import nu.pattern.OpenCV;
import qa.justtestlah.configuration.JustTestLahConfiguration;

@Component
public class OpenCVHooks extends AbstractCucumberHook {

  private static final Logger LOG = LoggerFactory.getLogger(OpenCVHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  @Override
  public void before(Scenario scenario) {
    if (configuration.isOpenCvEnabled()) {
      try {
        // load OpenCV library
        OpenCV.loadShared();
        OpenCV.loadLocally();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      } catch (ExceptionInInitializerError exception) {
        LOG.error("Error loading OpenCV libraries", exception);
      }
    }
  }
}
