package io.github.st2client.stream;

import io.github.st2client.auth.TokenProvider;
import io.github.st2client.internal.http.AuthHeadersInterceptor;
import io.github.st2client.internal.http.UrlPaths;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

/**
 * High-level SSE client for the StackStorm stream API ({@code /stream/v1/stream}). Supports
 * automatic reconnection via a configurable {@link ReconnectPolicy}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class St2StreamClient implements Closeable {

  private static final Logger log = LoggerFactory.getLogger(St2StreamClient.class);
  private static final String STREAM_PATH = "/stream";

  private final OkHttpClient okHttp;
  private final String baseUrl;
  private final TokenProvider tokenProvider;
  private final ReconnectPolicy reconnectPolicy;

  private volatile EventSource currentEventSource;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private volatile EventListener listener;
  private volatile int listenGeneration = 0;

  /**
   * Creates a stream client with the default exponential backoff reconnect policy.
   *
   * @param okHttp shared OkHttpClient
   * @param baseUrl stream API base URL (e.g. {@code http://host:9102/v1})
   * @param tokenProvider provides auth tokens for SSE connections
   */
  public St2StreamClient(OkHttpClient okHttp, String baseUrl, TokenProvider tokenProvider) {
    this(okHttp, baseUrl, tokenProvider, ReconnectPolicy.DEFAULT);
  }

  /**
   * Creates a stream client with a custom reconnect policy.
   *
   * @param okHttp shared OkHttpClient
   * @param baseUrl stream API base URL (e.g. {@code http://host:9102/v1})
   * @param tokenProvider provides auth tokens for SSE connections
   * @param reconnectPolicy strategy for reconnection delays
   */
  public St2StreamClient(
      OkHttpClient okHttp,
      String baseUrl,
      TokenProvider tokenProvider,
      ReconnectPolicy reconnectPolicy) {
    this.okHttp = Objects.requireNonNull(okHttp, "okHttp must not be null");
    this.baseUrl =
        UrlPaths.stripTrailingSlash(Objects.requireNonNull(baseUrl, "baseUrl must not be null"));
    this.tokenProvider = Objects.requireNonNull(tokenProvider, "tokenProvider must not be null");
    this.reconnectPolicy =
        Objects.requireNonNull(reconnectPolicy, "reconnectPolicy must not be null");
  }

  /**
   * Start listening to the given event types synchronously. Each invocation closes any previous
   * connection and replaces the listener.
   *
   * @param eventTypes event types to filter (e.g. "execution", "action"), or empty for all
   * @param listener callback for received events
   * @throws IllegalStateException if the client is already closed
   */
  public void listen(List<String> eventTypes, EventListener listener) {
    if (closed.get()) {
      throw new IllegalStateException("St2StreamClient is already closed");
    }
    synchronized (this) {
      closeCurrentEventSource();
      this.listener = listener;
      listenGeneration++;
      connect(eventTypes, listener, 0);
    }
  }

  /**
   * Start listening asynchronously. Returns a CompletableFuture that completes when the connection
   * is closed or fails.
   *
   * @param eventTypes event types to filter, or empty for all
   * @param listener callback for received events
   * @return a CompletableFuture that completes when the stream ends
   */
  public CompletableFuture<Void> listenAsync(List<String> eventTypes, EventListener listener) {
    if (closed.get()) {
      return CompletableFuture.failedFuture(
          new IllegalStateException("St2StreamClient is already closed"));
    }
    synchronized (this) {
      closeCurrentEventSource();
      this.listener = listener;
      listenGeneration++;
      CompletableFuture<Void> future = new CompletableFuture<>();
      connect(eventTypes, listener, 0, future);
      return future;
    }
  }

  /** Stop listening and close the underlying EventSource. */
  @Override
  public void close() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }
    synchronized (this) {
      closeCurrentEventSource();
    }
  }

  /** Cancels the current EventSource connection, if any. */
  private void closeCurrentEventSource() {
    EventSource es = currentEventSource;
    if (es != null) {
      es.cancel();
      currentEventSource = null;
    }
  }

  /**
   * Creates a synchronous SSE connection for the given event types.
   *
   * @param eventTypes the event types to subscribe to
   * @param listener the event listener
   * @param attempt the current connection attempt number
   */
  private void connect(List<String> eventTypes, EventListener listener, int attempt) {
    if (closed.get()) return;
    currentEventSource =
        EventSources.createFactory(okHttp)
            .newEventSource(
                buildRequest(eventTypes), createListener(eventTypes, listener, attempt, null));
  }

  /**
   * Creates an asynchronous SSE connection for the given event types.
   *
   * @param eventTypes the event types to subscribe to
   * @param listener the event listener
   * @param attempt the current connection attempt number
   * @param future the CompletableFuture to complete when the stream ends
   */
  private void connect(
      List<String> eventTypes,
      EventListener listener,
      int attempt,
      CompletableFuture<Void> future) {
    if (closed.get()) {
      future.complete(null);
      return;
    }
    currentEventSource =
        EventSources.createFactory(okHttp)
            .newEventSource(
                buildRequest(eventTypes), createListener(eventTypes, listener, attempt, future));
  }

  /**
   * Factory method that creates an EventSourceListener to bridge OkHttp SSE events to the
   * application-level {@link EventListener}.
   *
   * @param eventTypes the event types to subscribe to
   * @param listener the application-level event listener
   * @param attempt the current connection attempt number
   * @param asyncFuture the CompletableFuture for async mode, or null for sync mode
   * @return an OkHttp EventSourceListener
   */
  private EventSourceListener createListener(
      List<String> eventTypes,
      EventListener listener,
      int attempt,
      CompletableFuture<Void> asyncFuture) {
    return new EventSourceListener() {
      @Override
      public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        log.debug("SSE connection opened to {}", STREAM_PATH);
        response.close();
        currentEventSource = eventSource;
      }

      @Override
      public void onEvent(
          @NotNull EventSource eventSource, String id, String type, @NotNull String data) {
        Event event = Event.fromSseEvent(data, type);
        if (event != null) {
          listener.onEvent(event);
        }
      }

      @Override
      public void onClosed(@NotNull EventSource eventSource) {
        log.debug("SSE connection closed");
        if (closed.get()) {
          listener.onComplete();
          if (asyncFuture != null) {
            asyncFuture.complete(null);
          }
        } else {
          listener.onError(new IOException("SSE connection closed, reconnecting"));
          scheduleReconnect(eventTypes, listener, attempt + 1, asyncFuture);
        }
      }

      @Override
      public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
        log.warn(
            "SSE connection failure (attempt {}): {}",
            attempt + 1,
            t != null ? t.getMessage() : "HTTP " + (response != null ? response.code() : "?"));
        if (t != null) {
          listener.onError(t);
        } else if (response != null) {
          listener.onError(new IOException("SSE HTTP " + response.code()));
        }
        if (!closed.get()) {
          scheduleReconnect(eventTypes, listener, attempt + 1, asyncFuture);
        } else if (asyncFuture != null) {
          asyncFuture.complete(null);
        }
      }
    };
  }

  /**
   * Schedules a reconnection attempt after the configured delay.
   *
   * @param eventTypes the event types to re-subscribe to
   * @param listener the event listener
   * @param nextAttempt the next attempt number
   * @param asyncFuture the CompletableFuture for async mode, or null for sync mode
   */
  private void scheduleReconnect(
      List<String> eventTypes,
      EventListener listener,
      int nextAttempt,
      CompletableFuture<Void> asyncFuture) {
    long delay = reconnectPolicy.delayMs(nextAttempt);
    log.debug("Reconnecting after {}ms (attempt {})", delay, nextAttempt + 1);
    int generation = listenGeneration;
    Runnable reconnect =
        () -> {
          synchronized (this) {
            if (closed.get()) {
              if (asyncFuture != null) {
                asyncFuture.complete(null);
              }
              return;
            }
            if (generation != listenGeneration) return;
            closeCurrentEventSource();
            if (asyncFuture != null) {
              connect(eventTypes, listener, nextAttempt, asyncFuture);
            } else {
              connect(eventTypes, listener, nextAttempt);
            }
          }
        };
    Runnable afterAuth =
        () ->
            CompletableFuture.delayedExecutor(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
                .execute(reconnect);
    if (tokenProvider.supportsTokenRefresh()) {
      tokenProvider.refresh().whenComplete((token, error) -> afterAuth.run());
    } else {
      afterAuth.run();
    }
  }

  /**
   * Builds an HTTP GET request for the SSE stream with optional event type filters.
   *
   * @param eventTypes the event types to filter, or null/empty for all events
   * @return the configured HTTP request
   */
  private Request buildRequest(List<String> eventTypes) {
    StringBuilder url = new StringBuilder(baseUrl).append(STREAM_PATH);
    if (eventTypes != null && !eventTypes.isEmpty()) {
      url.append("?events=");
      for (int i = 0; i < eventTypes.size(); i++) {
        if (i > 0) url.append(",");
        url.append(URLEncoder.encode(eventTypes.get(i), StandardCharsets.UTF_8));
      }
    }
    Request.Builder builder = new Request.Builder().url(url.toString()).get();
    tokenProvider
        .getToken()
        .ifPresent(token -> builder.header(AuthHeadersInterceptor.HEADER_AUTH_TOKEN, token));
    tokenProvider
        .getApiKey()
        .ifPresent(key -> builder.header(AuthHeadersInterceptor.HEADER_API_KEY, key));
    return builder.build();
  }
}
