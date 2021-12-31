package qa.justtestlah.exception;

import java.util.Map;

/** Exception class for screen verification errors. */
public class ScreenVerificationException extends JustTestLahException {
  private static final long serialVersionUID = 1L;

  /**
   * @param identifier identifier which couldn't be located
   * @param rawLocator the raw locator (type and value) of the identifier element
   * @param screenName screen affected screen
   * @param timeout the timeout used for the verification
   */
  public ScreenVerificationException(
      String identifier, Map<String, Object> rawLocator, String screenName, int timeout) {
    super(
        String.format(
            "Expected element %s (%s:%s) is not displayed on %s after %s milliseconds",
            identifier, rawLocator.get("type"), rawLocator.get("value"), screenName, timeout));
  }
}
