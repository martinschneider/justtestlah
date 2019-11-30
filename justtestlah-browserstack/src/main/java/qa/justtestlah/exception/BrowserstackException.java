package qa.justtestlah.exception;

/** Exception class for Browserstack-related errors. */
public class BrowserstackException extends JustTestLahException {

  private static final long serialVersionUID = 1L;

  /** @param message exception message */
  public BrowserstackException(String message) {
    super(message);
  }

  /**
   * @param message exception message
   * @param exception wrapped {@link Throwable}
   */
  public BrowserstackException(String message, Exception exception) {
    super(message, exception);
  }
}
