package io.github.st2client;

import io.github.st2client.auth.TokenProvider;
import io.github.st2client.config.ClientConfig;
import io.github.st2client.config.RetryPolicy;
import io.github.st2client.exception.ConfigurationException;
import io.github.st2client.internal.http.CurlLoggingInterceptor;
import io.github.st2client.internal.http.SslTrust;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * Fluent builder for St2Client.
 *
 * <pre>{@code
 * St2Client client = St2Client.builder()
 *     .baseUrl("http://stackstorm.example.com")
 *     .username("admin").password("Ch@ngeMe")
 *     .build();
 * }</pre>
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class St2ClientBuilder {

  private static final Logger log = LoggerFactory.getLogger(St2ClientBuilder.class);
  static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 30;
  static final int DEFAULT_READ_TIMEOUT_SECONDS = 60;
  static final int DEFAULT_WRITE_TIMEOUT_SECONDS = 60;

  private String baseUrl, authUrl, apiUrl, streamUrl, apiVersion;
  private String token, apiKey, username, password;
  private String cacert;
  private boolean verifySsl = true;
  private boolean debug;
  private int connectTimeoutSeconds = DEFAULT_CONNECT_TIMEOUT_SECONDS;
  private int readTimeoutSeconds = DEFAULT_READ_TIMEOUT_SECONDS;
  private int writeTimeoutSeconds = DEFAULT_WRITE_TIMEOUT_SECONDS;
  private TokenProvider tokenProvider;
  private OkHttpClient okHttpClient;
  private RetryPolicy retryPolicy;
  private int maxIdleConnections = 5;
  private long keepAliveDurationMs = TimeUnit.MINUTES.toMillis(5);

  St2ClientBuilder() {}

  /**
   * Sets the base StackStorm URL. The auth, API, and stream URLs are derived from this value when
   * not set explicitly.
   *
   * @param v the base URL (e.g. {@code http://stackstorm.example.com})
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder baseUrl(String v) {
    this.baseUrl = v;
    return this;
  }

  /**
   * Sets the authentication service URL.
   *
   * @param v the auth URL (default derived from {@link #baseUrl})
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder authUrl(String v) {
    this.authUrl = v;
    return this;
  }

  /**
   * Sets the API service URL.
   *
   * @param v the API URL (default derived from {@link #baseUrl})
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder apiUrl(String v) {
    this.apiUrl = v;
    return this;
  }

  /**
   * Sets the stream service URL.
   *
   * @param v the stream URL (default derived from {@link #baseUrl})
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder streamUrl(String v) {
    this.streamUrl = v;
    return this;
  }

  /**
   * Sets the API version.
   *
   * @param v the API version (default {@code v1})
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder apiVersion(String v) {
    this.apiVersion = v;
    return this;
  }

  /**
   * Sets a pre-obtained authentication token.
   *
   * @param v the auth token
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder token(String v) {
    this.token = v;
    return this;
  }

  /**
   * Sets an API key for key-based authentication.
   *
   * @param v the API key
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder apiKey(String v) {
    this.apiKey = v;
    return this;
  }

  /**
   * Sets the username for password-based authentication.
   *
   * @param v the username
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder username(String v) {
    this.username = v;
    return this;
  }

  /**
   * Sets the password for password-based authentication.
   *
   * @param v the password
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder password(String v) {
    this.password = v;
    return this;
  }

  /**
   * Sets the path to a custom CA certificate file.
   *
   * @param v the CA certificate file path
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder cacert(String v) {
    this.cacert = v;
    return this;
  }

  /**
   * Enables or disables SSL certificate verification.
   *
   * <p>Setting to {@code false} requires the {@code ST2_ALLOW_INSECURE_SSL=true} environment
   * variable as a safety check.
   *
   * @param v {@code false} to skip SSL verification (development only)
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder verifySsl(boolean v) {
    this.verifySsl = v;
    return this;
  }

  /**
   * Enables debug mode which logs equivalent cURL commands.
   *
   * @param v {@code true} to enable debug logging
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder debug(boolean v) {
    this.debug = v;
    return this;
  }

  /**
   * Sets the connect timeout in seconds.
   *
   * @param seconds the connect timeout (default 30)
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder connectTimeout(int seconds) {
    this.connectTimeoutSeconds = seconds;
    return this;
  }

  /**
   * Sets the read timeout in seconds.
   *
   * @param seconds the read timeout (default 60)
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder readTimeout(int seconds) {
    this.readTimeoutSeconds = seconds;
    return this;
  }

  /**
   * Sets the write timeout in seconds.
   *
   * @param seconds the write timeout (default 60)
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder writeTimeout(int seconds) {
    this.writeTimeoutSeconds = seconds;
    return this;
  }

  /**
   * Sets a custom token provider, bypassing the default credential resolution.
   *
   * @param v the token provider
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder tokenProvider(TokenProvider v) {
    this.tokenProvider = v;
    return this;
  }

  /**
   * Sets a pre-configured OkHttpClient instance. When set, the builder will not create its own
   * client and will not close the provided instance on shutdown.
   *
   * @param v the OkHttpClient to use
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder okHttpClient(OkHttpClient v) {
    this.okHttpClient = v;
    return this;
  }

  /**
   * Sets a retry policy for failed HTTP requests.
   *
   * @param v the retry policy
   * @return this builder
   * @since 0.1.0
   */
  public St2ClientBuilder retryPolicy(RetryPolicy v) {
    this.retryPolicy = v;
    return this;
  }

  /**
   * Sets the maximum number of idle connections to keep in the connection pool.
   *
   * @param v the max idle connections (must be &ge; 0)
   * @return this builder
   * @throws IllegalArgumentException if {@code v < 0}
   * @since 0.1.0
   */
  public St2ClientBuilder maxIdleConnections(int v) {
    if (v < 0) throw new IllegalArgumentException("maxIdleConnections must be >= 0");
    this.maxIdleConnections = v;
    return this;
  }

  /**
   * Sets the keep-alive duration for idle connections.
   *
   * @param duration the duration value
   * @param unit the time unit for the duration
   * @return this builder
   * @throws IllegalArgumentException if {@code duration < 0}
   * @since 0.1.0
   */
  public St2ClientBuilder keepAliveDuration(long duration, TimeUnit unit) {
    if (duration < 0) throw new IllegalArgumentException("keepAliveDuration must be >= 0");
    this.keepAliveDurationMs = unit.toMillis(duration);
    return this;
  }

  /**
   * Builds the {@link St2Client} instance with the configured settings.
   *
   * @return a new St2Client
   * @since 0.1.0
   */
  public St2Client build() {
    ClientConfig config = buildConfig();
    boolean ownsOkHttp;
    OkHttpClient baseOkHttp;
    if (this.okHttpClient != null) {
      baseOkHttp = withDebugInterceptor(this.okHttpClient, config.isDebug());
      ownsOkHttp = false;
    } else {
      baseOkHttp = createOkHttp(config);
      ownsOkHttp = true;
    }
    return new St2Client(config, baseOkHttp, this.tokenProvider, ownsOkHttp, this.retryPolicy);
  }

  private OkHttpClient withDebugInterceptor(OkHttpClient client, boolean debugEnabled) {
    if (!debugEnabled) {
      return client;
    }
    return client.newBuilder().addInterceptor(new CurlLoggingInterceptor()).build();
  }

  private ClientConfig buildConfig() {
    return ClientConfig.builder()
        .baseUrl(baseUrl)
        .authUrl(authUrl)
        .apiUrl(apiUrl)
        .streamUrl(streamUrl)
        .apiVersion(apiVersion)
        .token(token)
        .apiKey(apiKey)
        .username(username)
        .password(password)
        .cacert(cacert)
        .verifySsl(verifySsl)
        .debug(debug)
        .build();
  }

  private OkHttpClient createOkHttp(ClientConfig config) {
    OkHttpClient.Builder builder =
        new OkHttpClient.Builder()
            .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
            .connectionPool(
                new ConnectionPool(maxIdleConnections, keepAliveDurationMs, TimeUnit.MILLISECONDS));

    String cacertPath = config.getCacert();
    if (!config.isVerifySsl()) {
      applyInsecureSsl(builder);
    } else if (cacertPath != null && !cacertPath.isEmpty()) {
      applyCustomSsl(builder, cacertPath);
    }

    if (config.isDebug()) {
      builder.addInterceptor(new CurlLoggingInterceptor());
    }

    return builder.build();
  }

  private void applyCustomSsl(OkHttpClient.Builder builder, String cacertPath) {
    try {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate caCert;
      try (FileInputStream fis = new FileInputStream(cacertPath)) {
        caCert = (X509Certificate) cf.generateCertificate(fis);
      }
      if (caCert == null) {
        throw new ConfigurationException("No certificate found in: " + cacertPath);
      }

      X509TrustManager trustManager = SslTrust.systemPlus(caCert);

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[] {trustManager}, null);
      builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
    } catch (Exception e) {
      throw new ConfigurationException("Failed to configure SSL with CA cert: " + cacertPath, e);
    }
  }

  private void applyInsecureSsl(OkHttpClient.Builder builder) {
    String envAllow = System.getenv("ST2_ALLOW_INSECURE_SSL");
    if (!"true".equalsIgnoreCase(envAllow)) {
      throw new ConfigurationException(
          "SSL verification disabled (verifySsl=false) but ST2_ALLOW_INSECURE_SSL is not set to 'true'. "
              + "Set ST2_ALLOW_INSECURE_SSL=true to confirm insecure mode is intentional.");
    }
    log.warn(
        "SSL certificate verification is DISABLED (verifySsl=false) -- do NOT use in production");
    try {
      X509TrustManager insecure =
          new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] c, String a) {}

            public void checkServerTrusted(X509Certificate[] c, String a) {}

            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }
          };
      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(null, new TrustManager[] {insecure}, null);
      builder.sslSocketFactory(ctx.getSocketFactory(), insecure);
      builder.hostnameVerifier((h, s) -> true);
    } catch (Exception e) {
      throw new ConfigurationException("Failed to configure insecure SSL", e);
    }
  }
}
