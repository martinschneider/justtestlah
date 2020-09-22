package qa.justtestlah.stubs;

/** Functionality for image template matching */
public interface TemplateMatcher {

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
