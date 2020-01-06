package qa.justtestlah.configuration;

import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;
import qa.justtestlah.browserstack.configuration.BrowserStackUrlBuilder;
import qa.justtestlah.browserstack.configuration.BrowserStackWebDriverBuilder;
import qa.justtestlah.browserstack.exception.BrowserstackException;

/**
 * Test for {@link BrowserStackWebDriverBuilder}.
 *
 * <p>Mocks Appium server and Browserstack app upload service.
 *
 * <p>TODO: verify capabilities
 */
public class BrowserStackWebDriverBuilderTest {

  private static final Logger LOG = LoggerFactory.getLogger(BrowserStackWebDriverBuilderTest.class);

  private static BrowserStackWebDriverBuilder target = new BrowserStackWebDriverBuilder();
  private static WireMockServer wireMockServer;
  private static int wireMockPort;

  @BeforeClass
  public static void setUp() throws IOException {
    // Wiremock setup
    wireMockPort = getAvailablePort();
    wireMockServer = new WireMockServer(options().port(wireMockPort));
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockPort);
  }

  @Before
  public void individualTestSetup() throws IOException {
    // Wiremock stubs
    stubFor(
        post("/session")
            .willReturn(
                ok(
                    "{\"value\":{\"capabilities\":{\"desired\":{\"platformName\":\"android\",\"app\":\"test.apk\",\"appActivity\":\"test\",\"appPackage\":\"test\"},\"platformName\":\"android\",\"app\":\"test.apk\",\"appActivity\":\"test\",\"appPackage\":\"test\",\"deviceName\":\"Google Pixel\"},\"sessionId\":\"sessionId\"}}")));
    stubFor(post("/upload").willReturn(ok("{app_url : \"test.apk\"}")));

    // Spring config values
    ReflectionTestUtils.setField(target, "username", "user");
    ReflectionTestUtils.setField(target, "accessKey", "key");
    ReflectionTestUtils.setField(
        target,
        "appPath",
        new DefaultResourceLoader().getResource("test.apk").getFile().getAbsolutePath());
    ReflectionTestUtils.setField(
        target, "uploadPath", "http://localhost:" + wireMockPort + "/upload");

    // Mock URL builder
    BrowserStackUrlBuilder mockUrlBuilder = mock(BrowserStackUrlBuilder.class);
    when(mockUrlBuilder.buildBrowserStackUrl(anyString(), anyString()))
        .thenReturn(new URL("http://localhost:" + wireMockPort));
    ReflectionTestUtils.setField(target, "browserStackUrlBuilder", mockUrlBuilder);
  }

  @After
  public void resetMocks() {
    WireMock.resetAllRequests();
  }

  @AfterClass
  public static void tearDown() {
    wireMockServer.stop();
  }

  @Test
  public void testAndroidDriver() throws IOException {
    target.getAndroidDriver();
    verify(1, postRequestedFor(urlEqualTo("/session")));
  }

  @Test
  public void testiOSDriver() throws IOException {
    target.getIOsDriver();
    verify(1, postRequestedFor(urlEqualTo("/session")));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testWebDriver() {
    target.getWebDriver();
    verify(0, postRequestedFor(urlEqualTo("/session")));
  }

  @Test
  public void testExistingAppAndroid() {
    ReflectionTestUtils.setField(target, "appPath", "bs://browserstack");
    target.getAndroidDriver();
    verify(1, postRequestedFor(urlEqualTo("/session")));
    verify(0, postRequestedFor(urlEqualTo("/upload")));
  }

  @Test
  public void testExistingAppiOS() {
    ReflectionTestUtils.setField(target, "appPath", "bs://browserstack");
    target.getIOsDriver();
    verify(1, postRequestedFor(urlEqualTo("/session")));
    verify(0, postRequestedFor(urlEqualTo("/upload")));
  }

  @Test(expected = BrowserstackException.class)
  public void testAppNull() {
    ReflectionTestUtils.setField(target, "appPath", null);
    target.getAndroidDriver();
    verify(0, postRequestedFor(urlEqualTo("/session")));
    verify(0, postRequestedFor(urlEqualTo("/upload")));
  }

  @Test(expected = BrowserstackException.class)
  public void testInvalidFileUploadResponseFromBrowserstack() {
    stubFor(post("/upload").willReturn(ok("")));
    target.getAndroidDriver();
    verify(1, postRequestedFor(urlEqualTo("/upload")));
    verify(0, postRequestedFor(urlEqualTo("/session")));
  }

  @Test(expected = BrowserstackException.class)
  public void testUnableToUpload() throws MalformedURLException {
    ReflectionTestUtils.setField(target, "uploadPath", "http://localhost_invalid/upload");
    target.getIOsDriver();
    verify(0, postRequestedFor(urlEqualTo("/session")));
    verify(0, postRequestedFor(urlEqualTo("/upload")));
  }

  @Test(expected = BrowserstackException.class)
  public void testUploadReturnsNon200() {
    stubFor(post("/upload").willReturn(notFound()));
    target.getIOsDriver();
  }

  private static int getAvailablePort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0); ) {
      int port = socket.getLocalPort();
      LOG.info("Using port {}", port);
      return port;
    }
  }
}
