package io.github.st2client.internal.http;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;

/** Tests for {@link CurlLoggingInterceptor} curl-format request/response logging. */
class CurlLoggingInterceptorTest extends BaseMockServerTest {

  private StringBuilder logOutput;
  private OkHttpClient client;

  @BeforeEach
  void setUp() throws Exception {
    logOutput = new StringBuilder();

    client =
        new OkHttpClient.Builder()
            .addInterceptor(new CurlLoggingInterceptor(logOutput::append))
            .build();
  }

  @AfterEach
  void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  @DisplayName("should fully redact X-Auth-Token regardless of case")
  void shouldRedactAuthToken() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"token\":\"leaked\"}"));

    Request request =
        new Request.Builder()
            .url(server.url("/test"))
            .header("x-auth-token", "super-secret-token-value")
            .header("Content-Type", "application/json")
            .build();

    try (Response ignored = client.newCall(request).execute()) {
      // nothing
    }

    String output = logOutput.toString();
    assertThat(output).containsIgnoringCase("x-auth-token: ***");
    assertThat(output).doesNotContain("super-secret-token-value");
    assertThat(output).doesNotContain("leaked");
    assertThat(output).contains("Content-Type: application/json");
  }

  @Test
  @DisplayName("should fully redact St2-Api-Key")
  void shouldRedactApiKey() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

    Request request =
        new Request.Builder()
            .url(server.url("/test"))
            .header("St2-Api-Key", "my-api-key-123456")
            .build();

    try (Response ignored = client.newCall(request).execute()) {
      // nothing
    }

    String output = logOutput.toString();
    assertThat(output).contains("St2-Api-Key: ***");
    assertThat(output).doesNotContain("my-api-key-123456");
  }

  @Test
  @DisplayName("should fully redact Authorization")
  void shouldRedactAuthorization() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

    Request request =
        new Request.Builder()
            .url(server.url("/test"))
            .header("Authorization", "Bearer long-token-here")
            .build();

    try (Response ignored = client.newCall(request).execute()) {
      // nothing
    }

    String output = logOutput.toString();
    assertThat(output).contains("Authorization: ***");
    assertThat(output).doesNotContain("long-token-here");
    assertThat(output).doesNotContain("Bearer");
  }

  @Test
  @DisplayName("should NOT redact non-sensitive headers")
  void shouldNotRedactNormalHeaders() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

    Request request =
        new Request.Builder()
            .url(server.url("/test"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build();

    try (Response ignored = client.newCall(request).execute()) {
      // nothing
    }

    String output = logOutput.toString();
    assertThat(output).contains("Content-Type: application/json");
    assertThat(output).contains("Accept: application/json");
    // no *** present
    assertThat(output).doesNotContain("***");
  }

  @Test
  @DisplayName("should redact short sensitive values entirely")
  void shouldRedactShortSensitiveValues() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

    Request request =
        new Request.Builder().url(server.url("/test")).header("X-Auth-Token", "short").build();

    try (Response ignored = client.newCall(request).execute()) {
      // nothing
    }

    String output = logOutput.toString();
    assertThat(output).contains("X-Auth-Token: ***");
    assertThat(output).doesNotContain("short");
  }

  @Test
  @DisplayName("should redact Cookie and Set-Cookie headers")
  void shouldRedactCookieHeaders() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

    Request request =
        new Request.Builder()
            .url(server.url("/test"))
            .header("Cookie", "session-id-abcdefg")
            .header("Set-Cookie", "token-value-xyz")
            .build();

    try (Response ignored = client.newCall(request).execute()) {
      // nothing
    }

    String output = logOutput.toString();
    assertThat(output).contains("Cookie: ***");
    assertThat(output).doesNotContain("session-id-abcdefg");
    assertThat(output).contains("Set-Cookie: ***");
    assertThat(output).doesNotContain("token-value-xyz");
  }

  @Test
  @DisplayName("should include request body in cURL output")
  void shouldIncludeRequestBody() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

    RequestBody body =
        RequestBody.create("{\"name\":\"test\"}", MediaType.parse("application/json"));
    Request request = new Request.Builder().url(server.url("/test")).post(body).build();

    try (Response ignored = client.newCall(request).execute()) {
      // nothing
    }

    String output = logOutput.toString();
    assertThat(output).contains("--data-binary");
    assertThat(output).contains("{\"name\":\"test\"}");
  }
}
