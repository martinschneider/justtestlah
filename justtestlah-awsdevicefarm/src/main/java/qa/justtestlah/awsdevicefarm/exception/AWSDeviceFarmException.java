package qa.justtestlah.awsdevicefarm.exception;

import qa.justtestlah.exception.JustTestLahException;

/** Exception class for AWS Devicefarm-related errors. */
public class AWSDeviceFarmException extends JustTestLahException {

  private static final long serialVersionUID = 1L;

  /** @param message exception message */
  public AWSDeviceFarmException(String message) {
    super(message);
  }

  /**
   * @param message exception message
   * @param exception wrapped {@link Throwable}
   */
  public AWSDeviceFarmException(String message, Throwable exception) {
    super(message, exception);
  }
}
