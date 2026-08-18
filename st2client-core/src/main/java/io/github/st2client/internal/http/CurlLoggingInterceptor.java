package io.github.st2client.internal.http;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.*;
import okio.Buffer;

/**
 * Logs the equivalent cURL command and HTTP status in debug mode. Sensitive header values are
 * replaced with {@code ***}. The response body is not logged or consumed.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class CurlLoggingInterceptor implements Interceptor {

  private static final Logger log = LoggerFactory.getLogger(CurlLoggingInterceptor.class);
  private static final Set<String> SENSITIVE_HEADERS =
      Set.of(
          AuthHeadersInterceptor.HEADER_AUTH_TOKEN,
          AuthHeadersInterceptor.HEADER_API_KEY,
          "Authorization",
          "Cookie",
          "Set-Cookie");

  private final Consumer<String> logConsumer;

  /** Creates a CurlLoggingInterceptor that logs via SLF4J at debug level. */
  public CurlLoggingInterceptor() {
    this(log::debug);
  }

  /**
   * Creates a CurlLoggingInterceptor with a custom log consumer.
   *
   * @param logConsumer the consumer that receives each log line
   */
  CurlLoggingInterceptor(Consumer<String> logConsumer) {
    this.logConsumer = logConsumer;
  }

  /**
   * Intercepts the request, logs a cURL representation, proceeds with the chain, and logs the
   * response status code.
   *
   * @param chain the interceptor chain
   * @return the response from the next interceptor or the HTTP call
   * @throws IOException if an I/O error occurs during the request
   */
  @NotNull
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    StringBuilder curl = new StringBuilder("curl");

    String method = request.method();
    if ("HEAD".equalsIgnoreCase(method)) {
      curl.append(" --head");
    } else if (!"GET".equalsIgnoreCase(method)) {
      curl.append(" -X ").append(escapeShell(method));
    }

    request
        .headers()
        .forEach(
            header ->
                curl.append(" -H ")
                    .append(
                        escapeShell(
                            header.getFirst()
                                + ": "
                                + redact(header.getFirst(), header.getSecond()))));

    RequestBody body = request.body();
    if (body != null) {
      try {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        String bodyStr = buffer.readUtf8();
        if (!bodyStr.isEmpty()) {
          curl.append(" --data-binary ").append(escapeShell(bodyStr));
        }
      } catch (IOException ignored) {
      }
    }

    curl.append(" ").append(escapeShell(request.url().toString()));

    logConsumer.accept("# -------- begin request ----------");
    logConsumer.accept(curl.toString());
    logConsumer.accept("# -------- end request ------------");

    long start = System.currentTimeMillis();
    Response response = chain.proceed(request);
    long duration = System.currentTimeMillis() - start;

    logConsumer.accept("# -------- begin response (" + duration + "ms) ----------");
    logConsumer.accept(String.valueOf(response.code()));
    logConsumer.accept("# -------- end response ------------");
    return response;
  }

  /**
   * Escapes a string for safe use in a shell command by wrapping it in single quotes and escaping
   * any embedded single quotes.
   *
   * @param s the string to escape
   * @return the shell-escaped string
   */
  private static String escapeShell(String s) {
    return "'" + s.replace("'", "'\''") + "'";
  }

  /**
   * Redacts sensitive header values entirely.
   *
   * @param name the header name
   * @param value the header value to potentially redact
   * @return the redacted value, or the original value if not sensitive
   */
  private static String redact(String name, String value) {
    if (isSensitive(name)) {
      return "***";
    }
    return value;
  }

  private static boolean isSensitive(String name) {
    if (name == null) {
      return false;
    }
    for (String header : SENSITIVE_HEADERS) {
      if (header.equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }
}
