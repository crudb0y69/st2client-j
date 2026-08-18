package io.github.st2client.exception;

import java.io.Serial;

/**
 * Base exception for all StackStorm client errors.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class St2ClientException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new St2ClientException.
   *
   * @param message the message
   * @since 0.1.0
   */
  public St2ClientException(String message) {
    super(message);
  }

  /**
   * Constructs a new St2ClientException.
   *
   * @param message the message
   * @param cause the cause
   * @since 0.1.0
   */
  public St2ClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
