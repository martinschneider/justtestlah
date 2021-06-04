package qa.justtestlah.visual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nu.pattern.OpenCV;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import qa.justtestlah.configuration.JustTestLahConfiguration;

public class TemplateMatcherTest {

  private static OpenCVTemplateMatcher target = new OpenCVTemplateMatcher();

  /** Initialise mocks and configuration. */
  @BeforeAll
  public static void init() {
    OpenCV.loadShared();
    OpenCV.loadLocally();
    if (Double.parseDouble(System.getProperty("java.specification.version")) < 12) {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    JustTestLahConfiguration configuration = mock(JustTestLahConfiguration.class);
    when(configuration.isOpenCvEnabled()).thenReturn(true);
    target.setConfiguration(configuration);
  }

  @Test
  public void testMatchPerfect() {
    assertThat(
            target
                .match(getPath("perfectMatch.png"), getPath("questionIcon.png"), 1, "sameSize")
                .isFound())
        .isTrue();
  }

  @Test
  public void testMatchScaleDown() {
    assertThat(
            target
                .match(getPath("smallerMatch.png"), getPath("questionIcon.png"), 0.9, "scaleDown")
                .isFound())
        .isTrue();
  }

  @Test
  public void testMatchScaleUp() {
    assertThat(
            target
                .match(getPath("largerMatch.png"), getPath("questionIcon.png"), 0.9, "scaleUp")
                .isFound())
        .isTrue();
  }

  @Test
  public void testBlurred() {
    assertThat(
            target
                .match(
                    getPath("perfectMatch.png"),
                    getPath("questionIcon_blurred.png"),
                    0.9,
                    "blurred")
                .isFound())
        .isTrue();
  }

  @Test
  public void testDistorted() {
    assertThat(
            target
                .match(
                    getPath("perfectMatch.png"),
                    getPath("questionIcon_distorted.png"),
                    0.75,
                    "distorted")
                .isFound())
        .isTrue();
  }

  @Test
  public void testRotated() {
    assertThat(
            target
                .match(
                    getPath("perfectMatch.png"),
                    getPath("questionIcon_rotated.png"),
                    0.85,
                    "rotated")
                .isFound())
        .isTrue();
  }

  @Test
  public void testNoMatch() {
    assertThat(target.match(getPath("noMatch.png"), getPath("questionIcon.png"), 0.5).isFound())
        .isFalse();
  }

  private String getPath(String fileName) {
    return this.getClass().getClassLoader().getResource("images/" + fileName).getFile();
  }
}
