package qa.justtestlah.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.exception.BrowserstackException;

/** Builder for the Browserstack URL. */
public class BrowserStackUrlBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BrowserStackUrlBuilder.class);

  public URL buildBrowserStackUrl(String accessKey, String username) {
    String url = "http://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";
    try {
      return new URL(url);
    } catch (MalformedURLException exception) {
      String error = String.format("Can't build Browserstack connection URL: %s", url);
      LOG.error(error, exception);
      throw new BrowserstackException(error, exception);
    }
  }
}
