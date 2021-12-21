package qa.justtestlah.visual.hooks;

import io.cucumber.java.Scenario;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.JustTestLahConfiguration;
import qa.justtestlah.hooks.AbstractCucumberHook;
import qa.justtestlah.hooks.HooksRegister;

@Component
public class OpenCVHooks extends AbstractCucumberHook implements InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(OpenCVHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  @Autowired private HooksRegister hooksRegister;

  @Override
  public void afterPropertiesSet() {
    hooksRegister.addHooks(this);
  }

  @Override
  public void before(Scenario scenario) {
    if (configuration.isOpenCvEnabled()) {
      try {
        // load OpenCV library
    	    OpenCV.loadShared();
    	    OpenCV.loadLocally();
    	    if (Double.parseDouble(System.getProperty("java.specification.version")) < 12) {
    	      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	    }
      } catch (ExceptionInInitializerError exception) {
        LOG.error("Error loading OpenCV libraries", exception);
      }
    }
  }
}
