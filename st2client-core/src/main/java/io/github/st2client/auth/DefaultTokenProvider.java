package io.github.st2client.auth;

import io.github.st2client.config.ClientConfig;
import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.http.HttpStatusCodes;
import io.github.st2client.model.Resource;
import io.github.st2client.model.Token;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Default implementation of {@link TokenProvider} that authenticates against the StackStorm auth
 * API and caches tokens using an atomic refresh mechanism.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class DefaultTokenProvider implements TokenProvider {

  private static final Logger log = LoggerFactory.getLogger(DefaultTokenProvider.class);
  private static final int MAX_RETRY_ATTEMPTS = 3;
  private static final Duration EXPIRY_SKEW = Duration.ofSeconds(60);

  private final ClientConfig config;
  private final OkHttpClient okHttp;
  private volatile String cachedToken;
  private volatile Instant tokenExpiry;
  private final String apiKey;
  private final AtomicReference<CompletableFuture<String>> inFlightRefresh =
      new AtomicReference<>();

  /**
   * Creates a DefaultTokenProvider with the given configuration and HTTP client.
   *
   * @param config the client configuration containing auth credentials
   * @param okHttp the shared OkHttpClient for making auth requests
   */
  public DefaultTokenProvider(ClientConfig config, OkHttpClient okHttp) {
    this.config = config;
    this.okHttp = okHttp;
    this.cachedToken = resolveToken();
    this.apiKey = resolveApiKey();
  }

  /**
   * Returns the current cached authentication token, if available.
   *
   * @return an Optional containing the cached token, or empty if not set
   */
  @Override
  public Optional<String> getToken() {
    if (needsProactiveRefresh()) {
      try {
        refresh().get(30, TimeUnit.SECONDS);
      } catch (Exception e) {
        log.warn("Proactive token refresh failed", e);
      }
    }
    return Optional.ofNullable(cachedToken);
  }

  /**
   * Overrides the current cached token with the given value.
   *
   * @param token the new authentication token
   */
  @Override
  public void setToken(String token) {
    this.cachedToken = token;
    this.tokenExpiry = null;
  }

  /**
   * Returns the configured API key for authentication, if available.
   *
   * @return an Optional containing the API key, or empty if not configured
   */
  @Override
  public Optional<String> getApiKey() {
    return Optional.ofNullable(apiKey);
  }

  /**
   * Returns whether token refresh is supported via password authentication or a static token
   * configuration.
   *
   * @return true if token refresh is possible, false otherwise
   */
  @Override
  public boolean supportsTokenRefresh() {
    return canAuthenticateWithPassword() || hasStaticToken();
  }

  /**
   * Initiates a token refresh asynchronously with retry logic. Uses an atomic reference to
   * deduplicate concurrent refresh attempts.
   *
   * @return a CompletableFuture that completes with the refreshed token
   */
  @Override
  public CompletableFuture<String> refresh() {
    for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
      CompletableFuture<String> existing = inFlightRefresh.get();
      if (existing != null && !existing.isDone()) {
        return existing;
      }

      CompletableFuture<String> future = new CompletableFuture<>();
      if (!inFlightRefresh.compareAndSet(existing, future)) {
        continue;
      }
      future.whenComplete((token, error) -> inFlightRefresh.compareAndSet(future, null));
      try {
        if (canAuthenticateWithPassword()) {
          String token = doAuthenticate();
          cachedToken = token;
          future.complete(token);
        } else if (hasStaticToken()) {
          String token = config.getToken();
          cachedToken = token;
          future.complete(token);
        } else {
          future.completeExceptionally(
              new IOException("No credentials configured for token refresh"));
        }
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
      return future;
    }
    return CompletableFuture.failedFuture(
        new IOException(
            "Failed to initiate token refresh after " + MAX_RETRY_ATTEMPTS + " attempts"));
  }

  /**
   * Callback invoked when an API request receives a 401 response. Clears the cached token to force
   * re-authentication on the next request.
   */
  @Override
  public void onUnauthorized() {
    this.cachedToken = null;
    this.tokenExpiry = null;
  }

  /**
   * Resolves the initial token from configuration or by authenticating.
   *
   * @return the resolved token, or null if not available
   */
  private String resolveToken() {
    if (hasStaticToken()) {
      return config.getToken();
    }
    return null;
  }

  /**
   * Resolves the API key from configuration.
   *
   * @return the API key, or null if not configured
   */
  private String resolveApiKey() {
    String key = config.getApiKey();
    return (key != null && !key.isEmpty()) ? key : null;
  }

  /**
   * Checks whether a static token is configured.
   *
   * @return true if a static token is available, false otherwise
   */
  private boolean hasStaticToken() {
    String token = config.getToken();
    return token != null && !token.isEmpty();
  }

  /**
   * Checks whether password-based authentication is available.
   *
   * @return true if both username and password are configured, false otherwise
   */
  private boolean canAuthenticateWithPassword() {
    return config.getUsername() != null && config.hasPassword();
  }

  /**
   * Authenticates with the given credentials and stores the obtained token.
   *
   * @param username the username to authenticate with
   * @param password the password to authenticate with
   * @throws IOException if a network or authentication error occurs
   */
  @Override
  public void authenticate(String username, String password) throws IOException {
    java.util.Objects.requireNonNull(username, "username must not be null");
    java.util.Objects.requireNonNull(password, "password must not be null");
    log.debug("Authenticating as user \"{}\"", username);
    char[] passwordChars = password.toCharArray();
    String credential;
    try {
      credential = basicAuth(username, passwordChars);
    } finally {
      Arrays.fill(passwordChars, '\0');
    }
    Request request =
        new Request.Builder()
            .url(config.getAuthUrl() + "/tokens")
            .header("Authorization", credential)
            .post(RequestBody.create("{}", MediaType.get("application/json")))
            .build();

    try (okhttp3.Response response = okHttp.newCall(request).execute()) {
      if (response.code() != HttpStatusCodes.OK.getCode()
          && response.code() != HttpStatusCodes.CREATED.getCode()) {
        String body = response.body() != null ? response.body().string() : "";
        throw new OperationFailureException(response.code(), "Auth failed: " + body);
      }
      String body = response.body() != null ? response.body().string() : "{}";
      Token token = Resource.fromJson(body, Token.class);
      onUnauthorized();
      rememberToken(token);
    }
  }

  /**
   * Performs password-based authentication against the StackStorm auth API.
   *
   * @return the authentication token obtained from the server
   * @throws IOException if a network or authentication error occurs
   */
  private String doAuthenticate() throws IOException {
    String username = config.getUsername();
    char[] passwordChars = config.getPassword();
    if (username == null || !config.hasPassword()) {
      throw new IOException("No credentials configured");
    }
    log.debug("Authenticating as user \"{}\"", username);

    String credential;
    try {
      credential = basicAuth(username, passwordChars);
    } finally {
      Arrays.fill(passwordChars, '\0');
    }
    Request request =
        new Request.Builder()
            .url(config.getAuthUrl() + "/tokens")
            .header("Authorization", credential)
            .post(RequestBody.create("{}", MediaType.get("application/json")))
            .build();

    try (okhttp3.Response response = okHttp.newCall(request).execute()) {
      if (response.code() != HttpStatusCodes.OK.getCode()
          && response.code() != HttpStatusCodes.CREATED.getCode()) {
        String body = response.body() != null ? response.body().string() : "";
        throw new OperationFailureException(response.code(), "Auth failed: " + body);
      }
      String body = response.body() != null ? response.body().string() : "{}";
      Token token = Resource.fromJson(body, Token.class);
      rememberToken(token);
      return token.getToken();
    }
  }

  private boolean needsProactiveRefresh() {
    return cachedToken != null
        && tokenExpiry != null
        && canAuthenticateWithPassword()
        && !Instant.now().plus(EXPIRY_SKEW).isBefore(tokenExpiry);
  }

  private void rememberToken(Token token) {
    this.cachedToken = token.getToken();
    this.tokenExpiry = parseExpiry(token.getExpiry());
  }

  private static Instant parseExpiry(String expiry) {
    if (expiry == null || expiry.isBlank()) {
      return null;
    }
    try {
      return Instant.parse(expiry);
    } catch (DateTimeParseException ignored) {
      try {
        return OffsetDateTime.parse(expiry).toInstant();
      } catch (DateTimeParseException e) {
        log.debug("Unable to parse token expiry {}", expiry);
        return null;
      }
    }
  }

  /**
   * Builds a Basic authorization header value, avoiding long-lived String retention of password
   * bytes.
   *
   * @param username the username
   * @param password the password characters (will be zeroed after use)
   * @return the Base64-encoded Basic authorization header value
   */
  private static String basicAuth(String username, char[] password) {
    byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
    ByteBuffer utf8 = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
    byte[] passwordBytes = new byte[utf8.remaining()];
    utf8.get(passwordBytes);
    try {
      byte[] credentialBytes = new byte[usernameBytes.length + 1 + passwordBytes.length];
      System.arraycopy(usernameBytes, 0, credentialBytes, 0, usernameBytes.length);
      credentialBytes[usernameBytes.length] = ':';
      System.arraycopy(
          passwordBytes, 0, credentialBytes, usernameBytes.length + 1, passwordBytes.length);
      return "Basic " + Base64.getEncoder().encodeToString(credentialBytes);
    } finally {
      Arrays.fill(passwordBytes, (byte) 0);
      if (utf8.hasArray()) {
        Arrays.fill(utf8.array(), (byte) 0);
      }
    }
  }
}
