package io.github.st2client.stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.st2client.auth.TokenProvider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/** Tests for {@link St2StreamClient} using MockWebServer. */
class St2StreamClientTest {

  private MockWebServer server;
  private St2StreamClient streamClient;

  @BeforeEach
  void setUp() throws IOException {
    server = new MockWebServer();
    server.start();
    OkHttpClient okHttp = new OkHttpClient();
    TokenProvider tokenProvider =
        new TokenProvider() {
          @Override
          public Optional<String> getToken() {
            return Optional.of("test-token");
          }

          @Override
          public Optional<String> getApiKey() {
            return Optional.empty();
          }

          @Override
          public void setToken(String token) {}

          @Override
          public CompletableFuture<String> refresh() {
            return CompletableFuture.completedFuture("test-token");
          }

          @Override
          public void onUnauthorized() {}
        };
    streamClient = new St2StreamClient(okHttp, server.url("/v1").toString(), tokenProvider);
  }

  @AfterEach
  void tearDown() throws IOException {
    streamClient.close();
    server.shutdown();
  }

  @Test
  void shouldParseSseEvent() throws Exception {
    String sseData =
        "{\"id\":\"evt-1\",\"timestamp\":1234567890,\"action\":{\"ref\":\"core.local\"}}";
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "text/event-stream")
            .setBody("event: execution\ndata: " + sseData + "\n\n"));

    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Event> receivedEvent = new AtomicReference<>();

    streamClient.listen(
        List.of("execution"),
        new EventListener() {
          @Override
          public void onEvent(Event event) {
            receivedEvent.set(event);
            latch.countDown();
          }

          @Override
          public void onError(Throwable error) {}

          @Override
          public void onComplete() {}
        });

    latch.await(5, TimeUnit.SECONDS);
    Event event = receivedEvent.get();
    assertThat(event).isNotNull();
    assertThat(event.eventType()).isEqualTo("execution");
    assertThat(event.data()).containsKey("action");
  }

  @Test
  void shouldHandleConnectionFailure() throws Exception {
    server.shutdown();

    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Throwable> receivedError = new AtomicReference<>();

    streamClient.listen(
        List.of("execution"),
        new EventListener() {
          @Override
          public void onEvent(Event event) {}

          @Override
          public void onError(Throwable error) {
            receivedError.set(error);
            latch.countDown();
          }

          @Override
          public void onComplete() {}
        });

    latch.await(5, TimeUnit.SECONDS);
    assertThat(receivedError.get()).isNotNull();
  }

  @Test
  void shouldCloseCleanly() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "text/event-stream")
            .setBody("event: execution\ndata: {\"id\":\"1\"}\n\n"));

    CountDownLatch openLatch = new CountDownLatch(1);
    AtomicInteger eventCount = new AtomicInteger(0);

    streamClient.listen(
        List.of("execution"),
        new EventListener() {
          @Override
          public void onEvent(Event event) {
            eventCount.incrementAndGet();
            openLatch.countDown();
          }

          @Override
          public void onError(Throwable error) {}

          @Override
          public void onComplete() {}
        });

    openLatch.await(2, TimeUnit.SECONDS);
    streamClient.close();
    assertThat(streamClient.toString()).isNotNull();
  }

  @Test
  void shouldBuildRequestWithEventTypes() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "text/event-stream")
            .setBody("event: execution\ndata: {\"id\":\"1\"}\n\n"));

    CountDownLatch latch = new CountDownLatch(1);

    streamClient.listen(
        List.of("execution", "action"),
        new EventListener() {
          @Override
          public void onEvent(Event event) {
            latch.countDown();
          }

          @Override
          public void onError(Throwable error) {}

          @Override
          public void onComplete() {}
        });

    latch.await(2, TimeUnit.SECONDS);

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).startsWith("/v1/stream");
    assertThat(request.getPath()).doesNotContain("/stream/v1/stream");
    assertThat(request.getPath()).contains("events=execution,action");
  }

  @Test
  void shouldSendApiKeyWhenConfigured() throws Exception {
    streamClient.close();
    TokenProvider apiKeyProvider =
        new TokenProvider() {
          @Override
          public Optional<String> getToken() {
            return Optional.empty();
          }

          @Override
          public Optional<String> getApiKey() {
            return Optional.of("st2-api-key-value");
          }

          @Override
          public void setToken(String token) {}

          @Override
          public CompletableFuture<String> refresh() {
            return CompletableFuture.completedFuture(null);
          }

          @Override
          public void onUnauthorized() {}

          @Override
          public boolean supportsTokenRefresh() {
            return false;
          }
        };
    streamClient =
        new St2StreamClient(new OkHttpClient(), server.url("/v1").toString(), apiKeyProvider);

    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "text/event-stream")
            .setBody("event: execution\ndata: {\"id\":\"1\"}\n\n"));

    CountDownLatch latch = new CountDownLatch(1);
    streamClient.listen(
        List.of(),
        new EventListener() {
          @Override
          public void onEvent(Event event) {
            latch.countDown();
          }

          @Override
          public void onError(Throwable error) {}

          @Override
          public void onComplete() {}
        });
    latch.await(2, TimeUnit.SECONDS);

    RecordedRequest request = server.takeRequest();
    assertThat(request.getHeader("St2-Api-Key")).isEqualTo("st2-api-key-value");
  }

  @Test
  void shouldBuildRequestWithAllEventsWhenEmpty() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "text/event-stream")
            .setBody("event: execution\ndata: {\"id\":\"1\"}\n\n"));

    CountDownLatch latch = new CountDownLatch(1);

    streamClient.listen(
        List.of(),
        new EventListener() {
          @Override
          public void onEvent(Event event) {
            latch.countDown();
          }

          @Override
          public void onError(Throwable error) {}

          @Override
          public void onComplete() {}
        });

    latch.await(2, TimeUnit.SECONDS);

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).doesNotContain("events=");
  }

  @Test
  void shouldThrowWhenClosedClientTriesToListen() {
    streamClient.close();

    assertThatThrownBy(
            () ->
                streamClient.listen(
                    List.of("execution"),
                    new EventListener() {
                      @Override
                      public void onEvent(Event event) {}

                      @Override
                      public void onError(Throwable error) {}

                      @Override
                      public void onComplete() {}
                    }))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("closed");
  }

  @Test
  void shouldReturnFutureForListenAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "text/event-stream")
            .setBody("event: execution\ndata: {\"id\":\"1\"}\n\n"));

    var future =
        streamClient.listenAsync(
            List.of("execution"),
            new EventListener() {
              @Override
              public void onEvent(Event event) {}

              @Override
              public void onError(Throwable error) {}

              @Override
              public void onComplete() {}
            });

    assertThat(future).isNotNull();
    streamClient.close();
    future.get(2, TimeUnit.SECONDS);
  }

  @Test
  void shouldReturnExceptionallyFutureWhenClosedAsync() {
    streamClient.close();

    var future =
        streamClient.listenAsync(
            List.of("execution"),
            new EventListener() {
              @Override
              public void onEvent(Event event) {}

              @Override
              public void onError(Throwable error) {}

              @Override
              public void onComplete() {}
            });

    assertThat(future.isCompletedExceptionally()).isTrue();
  }
}
