package io.github.st2client.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.config.ClientConfig;
import io.github.st2client.exception.OperationFailureException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Tests for {@link DefaultTokenProvider} covering token resolution, API key, authentication,
 * refresh, and invalidation.
 */
class DefaultTokenProviderTest extends BaseMockServerTest {

  @BeforeEach
  void setUp() throws IOException {}

  @Test
  void shouldReturnTokenFromConfig() {
    ClientConfig config = ClientConfig.builder().token("explicit-token").build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());

    assertThat(provider.getToken()).hasValue("explicit-token");
  }

  @Test
  void shouldReturnApiKeyFromConfig() {
    ClientConfig config = ClientConfig.builder().apiKey("my-api-key").build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());

    assertThat(provider.getApiKey()).hasValue("my-api-key");
  }

  @Test
  void shouldNotAuthenticateDuringConstruction() throws Exception {
    ClientConfig config =
        ClientConfig.builder()
            .authUrl("http://127.0.0.1:" + server.getPort())
            .username("admin")
            .password("pass")
            .build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());

    assertThat(provider.getToken()).isEmpty();
    assertThat(server.getRequestCount()).isZero();
  }

  @Test
  void shouldNotTreatUsernameOnlyAsPasswordAuth() {
    DefaultTokenProvider provider =
        new DefaultTokenProvider(
            ClientConfig.builder().username("admin").build(), new OkHttpClient());

    assertThat(provider.supportsTokenRefresh()).isFalse();
  }

  @Test
  void shouldAuthenticateAndCacheToken() throws Exception {
    String authUrl = "http://127.0.0.1:" + server.getPort();
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t1\",\"user\":\"admin\",\"token\":\"secret-token\","
                    + "\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));

    ClientConfig config =
        ClientConfig.builder().authUrl(authUrl).username("admin").password("pass").build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());
    assertThat(provider.refresh().get()).isEqualTo("secret-token");
    assertThat(provider.getToken()).hasValue("secret-token");
  }

  @Test
  void shouldReturnNullWhenNoCredentials() {
    ClientConfig config = ClientConfig.builder().build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());

    assertThat(provider.getToken()).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenAuthServerUnavailable() throws IOException {
    String port = String.valueOf(server.getPort());
    server.shutdown();
    ClientConfig config =
        ClientConfig.builder()
            .authUrl("http://localhost:" + port)
            .username("admin")
            .password("pass")
            .build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());
    assertThat(provider.getToken()).isEmpty();
  }

  @Test
  void shouldClearTokenOnSetNull() {
    DefaultTokenProvider provider =
        new DefaultTokenProvider(
            ClientConfig.builder().token("initial").build(), new OkHttpClient());
    assertThat(provider.getToken()).hasValue("initial");

    provider.setToken(null);
    assertThat(provider.getToken()).isEmpty();
  }

  @Test
  void shouldReturnNullApiKeyWhenNotSet() {
    DefaultTokenProvider provider =
        new DefaultTokenProvider(ClientConfig.builder().build(), new OkHttpClient());
    assertThat(provider.getApiKey()).isEmpty();
  }

  @Test
  void shouldThrowOnRefreshWhenAuthFails() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(401).setBody("{\"faultstring\":\"unauthorized\"}"));

    DefaultTokenProvider provider =
        new DefaultTokenProvider(
            ClientConfig.builder()
                .token("initial-token")
                .authUrl("http://localhost:" + server.getPort())
                .username("admin")
                .password("wrong")
                .build(),
            new OkHttpClient());

    assertThatThrownBy(() -> provider.refresh().get())
        .hasCauseInstanceOf(OperationFailureException.class);
  }

  @Test
  void shouldRefreshToken() throws Exception {
    String authUrl = "http://127.0.0.1:" + server.getPort();
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t2\",\"user\":\"admin\",\"token\":\"refreshed-token\","
                    + "\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));

    ClientConfig config =
        ClientConfig.builder()
            .authUrl(authUrl)
            .token("initial-token")
            .username("admin")
            .password("pass")
            .build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());

    assertThat(provider.getToken()).hasValue("initial-token");

    String newToken = provider.refresh().get();
    assertThat(newToken).isEqualTo("refreshed-token");
    assertThat(provider.getToken()).hasValue("refreshed-token");
  }

  @Test
  void shouldInvalidateOnUnauthorized() throws Exception {
    String authUrl = "http://127.0.0.1:" + server.getPort();
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t3\",\"user\":\"admin\",\"token\":\"new-token\","
                    + "\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));

    ClientConfig config =
        ClientConfig.builder()
            .authUrl(authUrl)
            .token("initial-token")
            .username("admin")
            .password("pass")
            .build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());

    assertThat(provider.getToken()).hasValue("initial-token");

    provider.onUnauthorized();
    assertThat(provider.getToken()).isEmpty();

    String newToken = provider.refresh().get();
    assertThat(newToken).isEqualTo("new-token");
    assertThat(provider.getToken()).hasValue("new-token");
  }

  @Test
  void shouldRefreshStaticTokenAfterUnauthorized() throws Exception {
    ClientConfig config = ClientConfig.builder().token("static-token").build();

    DefaultTokenProvider provider = new DefaultTokenProvider(config, new OkHttpClient());
    assertThat(provider.getToken()).hasValue("static-token");

    provider.onUnauthorized();
    assertThat(provider.getToken()).isEmpty();

    String refreshed = provider.refresh().get();
    assertThat(refreshed).isEqualTo("static-token");
    assertThat(provider.getToken()).hasValue("static-token");
  }

  @Test
  void shouldRefreshAgainAfterPreviousRefreshCompletes() throws Exception {
    String authUrl = "http://127.0.0.1:" + server.getPort();
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t1\",\"user\":\"admin\",\"token\":\"token-one\","
                    + "\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t2\",\"user\":\"admin\",\"token\":\"token-two\","
                    + "\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));

    DefaultTokenProvider provider =
        new DefaultTokenProvider(
            ClientConfig.builder().authUrl(authUrl).username("admin").password("pass").build(),
            new OkHttpClient());

    assertThat(provider.refresh().get()).isEqualTo("token-one");
    assertThat(provider.refresh().get()).isEqualTo("token-two");
  }

  @Test
  void shouldNotSupportTokenRefreshForApiKeyOnly() {
    DefaultTokenProvider provider =
        new DefaultTokenProvider(
            ClientConfig.builder().apiKey("key-only").build(), new OkHttpClient());
    assertThat(provider.supportsTokenRefresh()).isFalse();
  }

  @Test
  void shouldEncodeBasicAuthPasswordAsUtf8() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t1\",\"user\":\"admin\",\"token\":\"tok\","
                    + "\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));

    DefaultTokenProvider provider =
        new DefaultTokenProvider(
            ClientConfig.builder()
                .authUrl("http://127.0.0.1:" + server.getPort())
                .username("admin")
                .password("päss")
                .build(),
            new OkHttpClient());

    provider.refresh().get();
    RecordedRequest request = server.takeRequest();
    String header = request.getHeader("Authorization");
    assertThat(header).startsWith("Basic ");
    String decoded =
        new String(
            Base64.getDecoder().decode(header.substring("Basic ".length())),
            StandardCharsets.UTF_8);
    assertThat(decoded).isEqualTo("admin:päss");
  }

  @Test
  void shouldRefreshWhenCachedTokenIsExpired() throws Exception {
    String authUrl = "http://127.0.0.1:" + server.getPort();
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t1\",\"user\":\"admin\",\"token\":\"expired-token\","
                    + "\"expiry\":\"2020-01-01T00:00:00.000Z\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t2\",\"user\":\"admin\",\"token\":\"fresh-token\","
                    + "\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));

    DefaultTokenProvider provider =
        new DefaultTokenProvider(
            ClientConfig.builder().authUrl(authUrl).username("admin").password("pass").build(),
            new OkHttpClient());

    assertThat(provider.refresh().get()).isEqualTo("expired-token");
    assertThat(provider.getToken()).hasValue("fresh-token");
    assertThat(server.getRequestCount()).isEqualTo(2);
  }
}
