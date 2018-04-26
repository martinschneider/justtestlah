package io.github.martinschneider.yasew.visual;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import nu.pattern.OpenCV;

public class TemplateMatcherTest {

  private TemplateMatcher target = new TemplateMatcher();

  @BeforeClass
  public static void setup() {
    OpenCV.loadShared();
    OpenCV.loadLocally();
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  @Test
  public void testMatchPerfect() {

    assertThat(target.match(getPath("largerMatch.png"), getPath("icon.png"), 0.9)).isTrue();
    assertThat(target.match(getPath("perfectMatch.png"), getPath("icon.png"), 1)).isTrue();
  }

  @Test
  public void testMatchScaleDown() {
    assertThat(target.match(getPath("smallerMatch.png"), getPath("icon.png"), 0.9)).isTrue();
  }

  @Test
  public void testMatchScaleUp() {
    assertThat(target.match(getPath("largerMatch.png"), getPath("icon.png"), 0.9)).isTrue();
  }

  @Test
  public void testNoMatch() {
    assertThat(target.match(getPath("noMatch.png"), getPath("icon.png"), 0.5)).isFalse();
  }

  private String getPath(String fileName) {
    return this.getClass().getClassLoader().getResource("images/" + fileName).getFile();
  }
}
