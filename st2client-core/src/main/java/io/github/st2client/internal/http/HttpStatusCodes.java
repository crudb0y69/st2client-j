package io.github.st2client.internal.http;

/**
 * HTTP status codes commonly used in StackStorm API interactions. Each constant includes the
 * numeric status code and a human-readable reason phrase.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public enum HttpStatusCodes {

  // 2xx Success
  OK(200, "OK"),
  CREATED(201, "Created"),
  NO_CONTENT(204, "No Content"),

  // 3xx Redirection
  MOVED_PERMANENTLY(301, "Moved Permanently"),
  FOUND(302, "Found"),
  NOT_MODIFIED(304, "Not Modified"),

  // 4xx Client errors
  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  FORBIDDEN(403, "Forbidden"),
  NOT_FOUND(404, "Not Found"),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  CONFLICT(409, "Conflict"),
  UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
  TOO_MANY_REQUESTS(429, "Too Many Requests"),

  // 5xx Server errors
  INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
  BAD_GATEWAY(502, "Bad Gateway"),
  SERVICE_UNAVAILABLE(503, "Service Unavailable"),
  GATEWAY_TIMEOUT(504, "Gateway Timeout");

  private final int code;
  private final String reason;

  /**
   * Creates an HTTP status code enum constant.
   *
   * @param code the numeric HTTP status code
   * @param reason the human-readable reason phrase
   */
  HttpStatusCodes(int code, String reason) {
    this.code = code;
    this.reason = reason;
  }

  /**
   * Returns the numeric HTTP status code.
   *
   * @return the HTTP status code
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns the human-readable reason phrase for this status code.
   *
   * @return the reason phrase
   */
  public String getReason() {
    return reason;
  }
}
