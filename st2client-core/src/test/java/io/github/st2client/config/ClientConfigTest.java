package io.github.st2client.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.st2client.exception.ConfigurationException;

import org.junit.jupiter.api.Test;

/** Tests for {@link io.github.st2client.config.ClientConfig} builder defaults and overrides. */
class ClientConfigTest {

  @Test
  void shouldUseBuilderValueOverDefault() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://builder.example.com").build();
    assertThat(config.getBaseUrl()).isEqualTo("http://builder.example.com");
  }

  @Test
  void shouldUseDefaultWhenNothingSet() {
    ClientConfig config = ClientConfig.builder().build();
    assertThat(config.getBaseUrl()).isEqualTo(ClientConfig.DEFAULT_BASE_URL);
    assertThat(config.getApiVersion()).isEqualTo(ClientConfig.DEFAULT_API_VERSION);
  }

  @Test
  void shouldDeriveApiUrlFromBaseUrl() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://myhost").build();
    assertThat(config.getApiUrl())
        .isEqualTo(
            "http://myhost:"
                + ClientConfig.DEFAULT_API_PORT
                + "/"
                + ClientConfig.DEFAULT_API_VERSION);
  }

  @Test
  void shouldDeriveAuthUrlFromBaseUrl() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://myhost").build();
    assertThat(config.getAuthUrl()).isEqualTo("http://myhost:" + ClientConfig.DEFAULT_AUTH_PORT);
  }

  @Test
  void shouldDeriveStreamUrlFromBaseUrl() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://myhost").build();
    assertThat(config.getStreamUrl())
        .isEqualTo(
            "http://myhost:"
                + ClientConfig.DEFAULT_STREAM_PORT
                + "/"
                + ClientConfig.DEFAULT_API_VERSION);
  }

  @Test
  void shouldUseExplicitApiUrlOverDerived() {
    ClientConfig config =
        ClientConfig.builder().baseUrl("http://myhost").apiUrl("http://custom:9999/v2").build();
    assertThat(config.getApiUrl()).isEqualTo("http://custom:9999/v2");
  }

  @Test
  void shouldUseExplicitApiVersion() {
    ClientConfig config = ClientConfig.builder().apiVersion("v2").build();
    assertThat(config.getApiVersion()).isEqualTo("v2");
  }

  @Test
  void shouldSetAllAuthFields() {
    ClientConfig config =
        ClientConfig.builder().token("t1").apiKey("k1").username("u1").password("p1").build();
    assertThat(config.getToken()).isEqualTo("t1");
    assertThat(config.getApiKey()).isEqualTo("k1");
    assertThat(config.getUsername()).isEqualTo("u1");
    assertThat(new String(config.getPassword())).isEqualTo("p1");
  }

  @Test
  void shouldSetCacert() {
    ClientConfig config = ClientConfig.builder().cacert("/path/to/ca.pem").build();
    assertThat(config.getCacert()).isEqualTo("/path/to/ca.pem");
  }

  @Test
  void shouldSetDebug() {
    ClientConfig config = ClientConfig.builder().debug(true).build();
    assertThat(config.isDebug()).isTrue();
  }

  @Test
  void shouldDefaultToNotDebug() {
    ClientConfig config = ClientConfig.builder().build();
    assertThat(config.isDebug()).isFalse();
  }

  @Test
  void shouldHandleCustomApiVersionWithDerivedUrls() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://host").apiVersion("v2").build();
    assertThat(config.getApiUrl())
        .isEqualTo("http://host:" + ClientConfig.DEFAULT_API_PORT + "/v2");
    assertThat(config.getStreamUrl())
        .isEqualTo("http://host:" + ClientConfig.DEFAULT_STREAM_PORT + "/v2");
  }

  @Test
  void shouldStripTrailingSlashFromBaseUrl() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://myhost/").build();
    assertThat(config.getBaseUrl()).isEqualTo("http://myhost");
    assertThat(config.getApiUrl())
        .isEqualTo("http://myhost:" + ClientConfig.DEFAULT_API_PORT + "/v1");
    assertThat(config.getAuthUrl()).isEqualTo("http://myhost:" + ClientConfig.DEFAULT_AUTH_PORT);
  }

  @Test
  void shouldReportNoPasswordWhenUnset() {
    ClientConfig config = ClientConfig.builder().username("admin").build();
    assertThat(config.hasPassword()).isFalse();
    assertThat(config.getPassword()).isEmpty();
  }

  @Test
  void shouldReportPasswordWhenSet() {
    ClientConfig config = ClientConfig.builder().password("secret").build();
    assertThat(config.hasPassword()).isTrue();
  }

  @Test
  void allAuthFieldsShouldDefaultToNull() {
    ClientConfig config = ClientConfig.builder().build();
    assertThat(config.getToken()).isNull();
    assertThat(config.getApiKey()).isNull();
    assertThat(config.getUsername()).isNull();
    assertThat(config.getPassword()).isEmpty();
    assertThat(config.getCacert()).isNull();
  }

  @Test
  void verifySslShouldDefaultToTrue() {
    ClientConfig config = ClientConfig.builder().build();
    assertThat(config.isVerifySsl()).isTrue();
  }

  @Test
  void shouldSetVerifySsl() {
    ClientConfig config = ClientConfig.builder().verifySsl(false).build();
    assertThat(config.isVerifySsl()).isFalse();
  }

  @Test
  void shouldReplacePortWhenBaseUrlAlreadyHasPort() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://host:8080").build();
    assertThat(config.getApiUrl()).isEqualTo("http://host:9101/v1");
    assertThat(config.getAuthUrl()).isEqualTo("http://host:9100");
    assertThat(config.getStreamUrl()).isEqualTo("http://host:9102/v1");
  }

  @Test
  void shouldNotProduceDoublePort() {
    ClientConfig config = ClientConfig.builder().baseUrl("http://localhost:9101").build();
    assertThat(config.getApiUrl()).isEqualTo("http://localhost:9101/v1");
    assertThat(config.getApiUrl()).doesNotContain(":9101:9101");
  }

  @Test
  void shouldDerivePathBasedIngressUrls() {
    ClientConfig config = ClientConfig.builder().baseUrl("https://st2.example.com/api").build();
    assertThat(config.getApiUrl()).isEqualTo("https://st2.example.com/api/v1");
    assertThat(config.getAuthUrl()).isEqualTo("https://st2.example.com/auth");
    assertThat(config.getStreamUrl()).isEqualTo("https://st2.example.com/stream/v1");
  }

  @Test
  void shouldDeriveUrlsWhenExplicitOverridesAreBlank() {
    ClientConfig config =
        ClientConfig.builder()
            .baseUrl("http://st2.example.com/api")
            .apiUrl("")
            .authUrl("")
            .streamUrl("")
            .build();
    assertThat(config.getApiUrl()).isEqualTo("http://st2.example.com/api/v1");
    assertThat(config.getAuthUrl()).isEqualTo("http://st2.example.com/auth");
    assertThat(config.getStreamUrl()).isEqualTo("http://st2.example.com/stream/v1");
  }

  @Test
  void shouldKeepVersionedIngressApiPath() {
    ClientConfig config = ClientConfig.builder().baseUrl("https://st2.example.com/api/v1").build();
    assertThat(config.getApiUrl()).isEqualTo("https://st2.example.com/api/v1");
    assertThat(config.getAuthUrl()).isEqualTo("https://st2.example.com/auth");
    assertThat(config.getStreamUrl()).isEqualTo("https://st2.example.com/stream/v1");
  }

  @Test
  void shouldRejectInvalidBaseUrl() {
    assertThatThrownBy(() -> ClientConfig.builder().baseUrl("not a url").build())
        .isInstanceOf(ConfigurationException.class)
        .hasMessageContaining("baseUrl");
  }
}
