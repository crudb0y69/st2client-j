package io.github.st2client.config;

import io.github.st2client.internal.http.UrlPaths;

/**
 * Client configuration with three-tier precedence:
 *
 * <ol>
 *   <li>Builder setters (highest)
 *   <li>Environment variables (ST2_BASE_URL, ST2_AUTH_URL, etc.)
 *   <li>Hard-coded defaults (lowest)
 * </ol>
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ClientConfig {

  public static final String DEFAULT_BASE_URL = "http://127.0.0.1";
  public static final int DEFAULT_API_PORT = 9101;
  public static final int DEFAULT_AUTH_PORT = 9100;
  public static final int DEFAULT_STREAM_PORT = 9102;
  public static final String DEFAULT_API_VERSION = "v1";
  public static final String AUTH_TOKENS_PATH = "/tokens";

  private final String baseUrl;
  private final String authUrl;
  private final String apiUrl;
  private final String streamUrl;
  private final String apiVersion;
  private final String cacert;
  private final boolean verifySsl;
  private final String token;
  private final String apiKey;
  private final boolean debug;
  private final String username;
  private final char[] password;

  private ClientConfig(Builder builder) {
    this.baseUrl =
        UrlPaths.stripTrailingSlash(
            firstNonNull(builder.baseUrl, env("ST2_BASE_URL"), DEFAULT_BASE_URL));
    this.apiVersion = firstNonNull(builder.apiVersion, env("ST2_API_VERSION"), DEFAULT_API_VERSION);
    this.cacert = firstNonNull(builder.cacert, env("ST2_CACERT"));
    this.verifySsl = builder.verifySsl;
    this.debug = builder.debug;

    EndpointUrls.Derived derived = EndpointUrls.derive(this.baseUrl, this.apiVersion);
    this.apiUrl =
        UrlPaths.stripTrailingSlash(
            firstNonNull(builder.apiUrl, env("ST2_API_URL"), derived.apiUrl()));
    this.authUrl =
        UrlPaths.stripTrailingSlash(
            firstNonNull(builder.authUrl, env("ST2_AUTH_URL"), derived.authUrl()));
    this.streamUrl =
        UrlPaths.stripTrailingSlash(
            firstNonNull(builder.streamUrl, env("ST2_STREAM_URL"), derived.streamUrl()));

    this.token = firstNonNull(builder.token, env("ST2_AUTH_TOKEN"));
    this.apiKey = firstNonNull(builder.apiKey, env("ST2_API_KEY"));

    this.username = builder.username;
    this.password = builder.password != null ? builder.password.clone() : null;
  }

  /**
   * Returns the StackStorm base URL after trailing slashes are stripped.
   *
   * @return the base URL
   * @since 0.1.0
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * Returns the auth service URL.
   *
   * @return the auth URL
   * @since 0.1.0
   */
  public String getAuthUrl() {
    return authUrl;
  }

  /**
   * Returns the API service URL.
   *
   * @return the API URL
   * @since 0.1.0
   */
  public String getApiUrl() {
    return apiUrl;
  }

  /**
   * Returns the stream service URL.
   *
   * @return the stream URL
   * @since 0.1.0
   */
  public String getStreamUrl() {
    return streamUrl;
  }

  /**
   * Returns the API version segment, such as {@code v1}.
   *
   * @return the API version
   * @since 0.1.0
   */
  public String getApiVersion() {
    return apiVersion;
  }

  /**
   * Returns the path to a custom CA certificate, or {@code null} if unset.
   *
   * @return the CA certificate path, or {@code null}
   * @since 0.1.0
   */
  public String getCacert() {
    return cacert;
  }

  /**
   * Returns whether TLS certificates are verified.
   *
   * @return {@code true} if SSL verification is enabled
   * @since 0.1.0
   */
  public boolean isVerifySsl() {
    return verifySsl;
  }

  /**
   * Returns the configured static auth token, or {@code null} if unset.
   *
   * @return the token, or {@code null}
   * @since 0.1.0
   */
  public String getToken() {
    return token;
  }

  /**
   * Returns the configured API key, or {@code null} if unset.
   *
   * @return the API key, or {@code null}
   * @since 0.1.0
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * Returns whether cURL-style debug logging is enabled.
   *
   * @return {@code true} if debug logging is enabled
   * @since 0.1.0
   */
  public boolean isDebug() {
    return debug;
  }

  /**
   * Returns the configured username, or {@code null} if unset.
   *
   * @return the username, or {@code null}
   * @since 0.1.0
   */
  public String getUsername() {
    return username;
  }

  private static final char[] EMPTY_PASSWORD = new char[0];

  /**
   * Returns a copy of the password char array
   *
   * @return the char[]
   * @since 0.1.0
   */
  public char[] getPassword() {
    return password != null ? password.clone() : EMPTY_PASSWORD;
  }

  /**
   * Returns whether a non-empty password is configured.
   *
   * @return true if a password is present
   * @since 0.1.0
   */
  public boolean hasPassword() {
    return password != null && password.length > 0;
  }

  /**
   * Creates a new Builder instance
   *
   * @return the builder
   * @since 0.1.0
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Client configuration with three-tier precedence:
   *
   * <ol>
   *   <li>Builder setters (highest)
   *   <li>Environment variables (ST2_BASE_URL, ST2_AUTH_URL, etc.)
   *   <li>Hard-coded defaults (lowest)
   * </ol>
   *
   * @author crudb0y69
   * @since 0.1.0
   */
  public static class Builder {
    private String baseUrl;
    private String authUrl;
    private String apiUrl;
    private String streamUrl;
    private String apiVersion;
    private String cacert;
    private boolean verifySsl = true;
    private String token;
    private String apiKey;
    private String username;
    private char[] password;
    private boolean debug;

    /**
     * Base url.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder baseUrl(String v) {
      this.baseUrl = v;
      return this;
    }

    /**
     * Auth url.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder authUrl(String v) {
      this.authUrl = v;
      return this;
    }

    /**
     * Api url.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder apiUrl(String v) {
      this.apiUrl = v;
      return this;
    }

    /**
     * Stream url.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder streamUrl(String v) {
      this.streamUrl = v;
      return this;
    }

    /**
     * Api version.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder apiVersion(String v) {
      this.apiVersion = v;
      return this;
    }

    /**
     * Cacert.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder cacert(String v) {
      this.cacert = v;
      return this;
    }

    /**
     * Verify ssl.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder verifySsl(boolean v) {
      this.verifySsl = v;
      return this;
    }

    /**
     * Token.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder token(String v) {
      this.token = v;
      return this;
    }

    /**
     * Api key.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder apiKey(String v) {
      this.apiKey = v;
      return this;
    }

    /**
     * Username.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder username(String v) {
      this.username = v;
      return this;
    }

    /**
     * Password.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder password(String v) {
      this.password = v != null ? v.toCharArray() : null;
      return this;
    }

    /**
     * Debug.
     *
     * @param v the v
     * @return the builder
     * @since 0.1.0
     */
    public Builder debug(boolean v) {
      this.debug = v;
      return this;
    }

    /**
     * Builds the ClientConfig with the configured values
     *
     * @return the clientconfig
     * @since 0.1.0
     */
    public ClientConfig build() {
      return new ClientConfig(this);
    }
  }

  private static String env(String name) {
    String v = System.getenv(name);
    return (v != null && !v.isEmpty()) ? v : null;
  }

  @SafeVarargs
  private static <T> T firstNonNull(T... values) {
    for (T v : values) {
      if (v == null) {
        continue;
      }
      if (v instanceof String s && s.isEmpty()) {
        continue;
      }
      return v;
    }
    return null;
  }
}
