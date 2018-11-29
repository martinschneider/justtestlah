package io.github.martinschneider.justtestlah.visual;

public interface TemplateMatcher {

  public static final int MIN_IMAGE_WIDTH = 320;
  public static final int MAX_IMAGE_WIDTH = 3200;
  public static final String DATE_PATTERN = "yyyy-MM-dd HH.mm.ss";
  public static final String FILE_EXTENSION = "png";

  /**
   * Check whether the template appears anywhere within the target image.
   *
   * @param targetFile path to the target file
   * @param templateFile path to the template file
   * @param threshold matching threshold
   * @return {@link Match}
   */
  Match match(String targetFile, String templateFile, double threshold);

  /**
   * Check whether the template appears anywhere within the target image.
   *
   * @param targetFile path to the target file
   * @param templateFile path to the template file
   * @param threshold matching threshold
   * @param description of the check
   * @return {@link Match}
   */
  Match match(String targetFile, String templateFile, double threshold, String description);
}
