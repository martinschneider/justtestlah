package qa.justtestlah.mobile.tools.exception;

/** Exception class for JustTestLah runtime exceptions. */
public class MobileToolsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** @param message exception message */
  public MobileToolsException(String message) {
    super(message);
  }

  /**
   * @param message exception message
   * @param exception wrapped {@link Throwable}
   */
  public MobileToolsException(String message, Throwable exception) {
    super(message, exception);
  }
}
