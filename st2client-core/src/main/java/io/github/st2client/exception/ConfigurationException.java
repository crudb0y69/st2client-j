package io.github.st2client.exception;

import java.io.Serial;

/**
 * Thrown when there is a configuration error.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ConfigurationException extends St2ClientException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new ConfigurationException.
   *
   * @param message the message
   * @since 0.1.0
   */
  public ConfigurationException(String message) {
    super(message);
  }

  /**
   * Constructs a new ConfigurationException.
   *
   * @param message the message
   * @param cause the cause
   * @since 0.1.0
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
