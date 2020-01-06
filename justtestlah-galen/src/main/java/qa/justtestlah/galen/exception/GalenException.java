package qa.justtestlah.galen.exception;

import qa.justtestlah.exception.JustTestLahException;

/** Exception class for Galen-related errors. */
public class GalenException extends JustTestLahException {

  private static final long serialVersionUID = 1L;

  /** @param message exception message */
  public GalenException(String message) {
    super(message);
  }

  /**
   * @param message exception message
   * @param exception wrapped {@link Throwable}
   */
  public GalenException(String message, Exception exception) {
    super(message, exception);
  }
}
