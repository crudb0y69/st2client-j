package io.github.st2client.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Configuration properties for the StackStorm client under the {@code st2.*} prefix.
 *
 * <p>Binds to {@code application.yml} or {@code application.properties} properties prefixed with
 * {@code st2}. The {@code st2.base-url} property activates the auto-configuration.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
@Validated
@ConfigurationProperties(prefix = "st2")
public class St2Properties {

  /** Base URL of the StackStorm server. Required to activate auto-configuration. */
  @NotBlank private String baseUrl;

  /**
   * Auth service URL. Host-only {@code baseUrl} derives {@code {host}:9100}. Path-based ingress
   * derives {@code {origin}/auth}.
   */
  private String authUrl;

  /**
   * API service URL. Host-only {@code baseUrl} derives {@code {host}:9101/v1}. Path-based ingress
   * derives {@code {origin}{/path}/v1}.
   */
  private String apiUrl;

  /**
   * Stream service URL. Host-only {@code baseUrl} derives {@code {host}:9102/v1}. Path-based
   * ingress derives {@code {origin}/stream/v1}.
   */
  private String streamUrl;

  /** API version. Defaults to {@code v1}. */
  private String apiVersion = io.github.st2client.config.ClientConfig.DEFAULT_API_VERSION;

  /** Enables debug mode which logs equivalent cURL commands. */
  private boolean debug;

  /** Connect timeout in seconds. Defaults to {@code 10}. */
  private int connectTimeout = 10;

  /** Read timeout in seconds. Defaults to {@code 30}. */
  private int readTimeout = 30;

  /** Write timeout in seconds. Defaults to {@code 30}. */
  private int writeTimeout = 30;

  private Auth auth = new Auth();
  private Ssl ssl;
  private Health health = new Health();
  private Async async = new Async();

  /** Returns the base URL. */
  public String getBaseUrl() {
    return baseUrl;
  }

  /** Sets the base URL. */
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /** Returns the auth service URL. */
  public String getAuthUrl() {
    return authUrl;
  }

  /** Sets the auth service URL. */
  public void setAuthUrl(String authUrl) {
    this.authUrl = authUrl;
  }

  /** Returns the API service URL. */
  public String getApiUrl() {
    return apiUrl;
  }

  /** Sets the API service URL. */
  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  /** Returns the stream service URL. */
  public String getStreamUrl() {
    return streamUrl;
  }

  /** Sets the stream service URL. */
  public void setStreamUrl(String streamUrl) {
    this.streamUrl = streamUrl;
  }

  /** Returns the API version. */
  public String getApiVersion() {
    return apiVersion;
  }

  /** Sets the API version. */
  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  /** Returns whether debug mode is enabled. */
  public boolean isDebug() {
    return debug;
  }

  /** Enables or disables debug mode. */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /** Returns the connect timeout in seconds. */
  public int getConnectTimeout() {
    return connectTimeout;
  }

  /** Sets the connect timeout in seconds. */
  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  /** Returns the read timeout in seconds. */
  public int getReadTimeout() {
    return readTimeout;
  }

  /** Sets the read timeout in seconds. */
  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  /** Returns the write timeout in seconds. */
  public int getWriteTimeout() {
    return writeTimeout;
  }

  /** Sets the write timeout in seconds. */
  public void setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
  }

  /** Returns the health indicator configuration. */
  public Health getHealth() {
    return health;
  }

  /** Sets the health indicator configuration. */
  public void setHealth(Health health) {
    this.health = health;
  }

  /** Returns the authentication configuration. */
  public Auth getAuth() {
    return auth;
  }

  /** Sets the authentication configuration. */
  public void setAuth(Auth auth) {
    this.auth = auth;
  }

  /** Returns the SSL/TLS configuration. */
  public Ssl getSsl() {
    if (ssl == null) {
      ssl = new Ssl();
    }
    return ssl;
  }

  /** Sets the SSL/TLS configuration. */
  public void setSsl(Ssl ssl) {
    this.ssl = java.util.Objects.requireNonNull(ssl, "ssl must not be null");
  }

  /** Returns the async thread-pool configuration. */
  public Async getAsync() {
    return async;
  }

  /** Sets the async thread-pool configuration. */
  public void setAsync(Async async) {
    this.async = async;
  }

  /**
   * Health indicator settings for the Actuator endpoint.
   *
   * @since 0.1.0
   */
  public static class Health {
    /** Whether to register {@link St2HealthIndicator}. Defaults to {@code true}. */
    private boolean enabled = true;

    /** Health probe result cache TTL in seconds. Defaults to {@code 10}. */
    private int cacheSeconds = 10;

    /** Returns whether the health indicator is enabled. */
    public boolean isEnabled() {
      return enabled;
    }

    /** Sets whether the health indicator is enabled. */
    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    /** Returns the health probe cache TTL in seconds. */
    public int getCacheSeconds() {
      return cacheSeconds;
    }

    /** Sets the health probe cache TTL in seconds. */
    public void setCacheSeconds(int cacheSeconds) {
      this.cacheSeconds = cacheSeconds;
    }
  }

  /**
   * Authentication-specific settings.
   *
   * @since 0.1.0
   */
  public static class Auth {
    /** Pre-obtained auth token. Takes precedence over username/password. */
    private String token;

    /** API key for key-based authentication. */
    private String apiKey;

    /** Username for password-based authentication. */
    private String username;

    /** Password for password-based authentication. */
    private String password;

    /** Returns the pre-obtained auth token. */
    public String getToken() {
      return token;
    }

    /** Sets the pre-obtained auth token. */
    public void setToken(String token) {
      this.token = token;
    }

    /** Returns the API key. */
    public String getApiKey() {
      return apiKey;
    }

    /** Sets the API key. */
    public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
    }

    /** Returns the username. */
    public String getUsername() {
      return username;
    }

    /** Sets the username. */
    public void setUsername(String username) {
      this.username = username;
    }

    /** Returns the password. */
    public String getPassword() {
      return password;
    }

    /** Sets the password. */
    public void setPassword(String password) {
      this.password = password;
    }
  }

  /**
   * SSL/TLS configuration.
   *
   * @since 0.1.0
   */
  public static class Ssl {
    /** Path to a custom CA certificate file (appended to the system truststore). */
    private String cacert;

    /**
     * Whether to verify SSL certificates. Set to {@code false} to skip verification (development
     * only). Defaults to {@code true}.
     */
    private boolean verifySsl = true;

    /** Returns the custom CA certificate path. */
    public String getCacert() {
      return cacert;
    }

    /** Sets the custom CA certificate path. */
    public void setCacert(String cacert) {
      this.cacert = cacert;
    }

    /** Returns whether SSL verification is enabled. */
    public boolean isVerifySsl() {
      return verifySsl;
    }

    /** Enables or disables SSL verification. */
    public void setVerifySsl(boolean verifySsl) {
      this.verifySsl = verifySsl;
    }
  }

  /**
   * Async thread-pool configuration.
   *
   * @since 0.1.0
   */
  public static class Async {
    /** Whether to enable the async thread pool. Defaults to {@code true}. */
    private boolean enabled = true;

    /** Core pool size. Defaults to {@code 4}. */
    @Min(1)
    private int corePoolSize = 4;

    /** Maximum pool size. Defaults to {@code 8}. */
    @Min(1)
    private int maxPoolSize = 8;

    /** Work queue capacity. Defaults to {@code 100}. */
    @Min(0)
    private int queueCapacity = 100;

    /** Thread name prefix. Defaults to {@code st2-async-}. */
    private String threadNamePrefix = "st2-async-";

    /** Returns whether the async thread pool is enabled. */
    public boolean isEnabled() {
      return enabled;
    }

    /** Enables or disables the async thread pool. */
    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    /** Returns the core pool size. */
    public int getCorePoolSize() {
      return corePoolSize;
    }

    /** Sets the core pool size. */
    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    /** Returns the maximum pool size. */
    public int getMaxPoolSize() {
      return maxPoolSize;
    }

    /** Sets the maximum pool size. */
    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }

    /** Returns the work queue capacity. */
    public int getQueueCapacity() {
      return queueCapacity;
    }

    /** Sets the work queue capacity. */
    public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
    }

    /** Returns the thread name prefix. */
    public String getThreadNamePrefix() {
      return threadNamePrefix;
    }

    /** Sets the thread name prefix. */
    public void setThreadNamePrefix(String threadNamePrefix) {
      this.threadNamePrefix = threadNamePrefix;
    }
  }
}
