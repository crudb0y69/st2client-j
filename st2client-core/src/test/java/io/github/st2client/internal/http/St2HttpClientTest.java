package io.github.st2client.internal.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.config.RetryPolicy;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;

/** Tests for {@link St2HttpClient} using MockWebServer. */
class St2HttpClientTest extends BaseMockServerTest {
  private St2HttpClient http;

  @BeforeEach
  void setUp() throws IOException {
    http = new St2HttpClient(server.url("").toString(), new OkHttpClient());
  }

  @Test
  void shouldGetSync() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));
    Response r = http.get("/api/test", null);
    assertThat(r.code()).isEqualTo(200);
    assertThat(r.body().string()).contains("ok");
    r.close();
  }

  @Test
  void shouldPostSync() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"created\":true}"));
    Response r = http.post("/api/test", Map.of("name", "val"));
    assertThat(r.code()).isEqualTo(200);
    r.close();
  }

  @Test
  void shouldGetAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"async\":true}"));
    Response r = http.getAsync("/api/test", null).get();
    assertThat(r.code()).isEqualTo(200);
    r.close();
  }

  @Test
  void shouldCancelAsyncRequest() {
    server.enqueue(
        new MockResponse().setBodyDelay(5, java.util.concurrent.TimeUnit.SECONDS).setBody("{}"));
    CompletableFuture<Response> future = http.getAsync("/api/slow", null);
    assertThat(future.cancel(true)).isTrue();
    assertThat(future).isCancelled();
  }

  @Test
  void shouldPostAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
    Response r = http.postAsync("/api/test", Map.of("k", "v")).get();
    assertThat(r.code()).isEqualTo(200);
    r.close();
  }

  @Test
  void shouldHandleErrorAsync() {
    server.enqueue(
        new MockResponse().setResponseCode(500).setBody("{\"faultstring\":\"async error\"}"));
    CompletableFuture<Response> future = http.getAsync("/api/bad", null);
    assertThatThrownBy(() -> future.get())
        .hasCauseInstanceOf(io.github.st2client.exception.OperationFailureException.class);
  }

  @Test
  void shouldRetryAsyncGetOnServiceUnavailable() throws Exception {
    RetryPolicy policy =
        RetryPolicy.builder()
            .maxRetries(2)
            .initialDelayMs(1)
            .maxDelayMs(5)
            .retryOnStatus(Set.of(503))
            .build();
    http = new St2HttpClient(server.url("").toString(), new OkHttpClient(), policy);

    server.enqueue(new MockResponse().setResponseCode(503).setBody("busy"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));

    Response r = http.getAsync("/api/test", null).get();
    assertThat(r.code()).isEqualTo(200);
    assertThat(r.body().string()).contains("ok");
    r.close();
    assertThat(server.getRequestCount()).isEqualTo(2);
  }

  @Test
  void shouldDeleteSync() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(204));
    Response r = http.delete("/api/del");
    assertThat(r.code()).isEqualTo(204);
    r.close();
  }

  @Test
  void shouldPutSync() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"updated\":true}"));
    Response r = http.put("/api/put", Map.of("k", "v"));
    assertThat(r.code()).isEqualTo(200);
    r.close();
  }
}
