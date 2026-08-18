package io.github.st2client.exception;

import java.io.Serial;

/**
 * Thrown when a StackStorm API operation fails. Contains the HTTP status code and the server's
 * faultstring for diagnostics. Maps to Python {@code
 * st2client.exceptions.operations.OperationFailureException}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class OperationFailureException extends St2ClientException {

  @Serial private static final long serialVersionUID = 1L;

  private final int statusCode;
  private final String faultString;

  /**
   * Constructs a new OperationFailureException.
   *
   * @param statusCode the statusCode
   * @param message the message
   * @since 0.1.0
   */
  public OperationFailureException(int statusCode, String message) {
    super("HTTP " + statusCode + ": " + message);
    this.statusCode = statusCode;
    this.faultString = message;
  }

  /**
   * Constructs a new OperationFailureException.
   *
   * @param message the message
   * @since 0.1.0
   */
  public OperationFailureException(String message) {
    super(message);
    this.statusCode = 0;
    this.faultString = message;
  }

  /**
   * Returns the HTTP status code from the failed response, or {@code 0} if none was set.
   *
   * @return the status code
   * @since 0.1.0
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Returns the server {@code faultstring}, or the exception message when none was parsed.
   *
   * @return the fault string
   * @since 0.1.0
   */
  public String getFaultString() {
    return faultString;
  }
}
