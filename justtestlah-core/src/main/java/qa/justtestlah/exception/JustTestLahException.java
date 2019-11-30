package qa.justtestlah.exception;

/** Exception class for JustTestLah runtime exceptions. */
public class JustTestLahException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** @param message exception message */
  public JustTestLahException(String message) {
    super(message);
  }

  /**
   * @param message exception message
   * @param exception wrapped {@link Throwable}
   */
  public JustTestLahException(String message, Throwable exception) {
    super(message, exception);
  }
}
