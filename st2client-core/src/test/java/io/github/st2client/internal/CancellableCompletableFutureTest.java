package io.github.st2client.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import okhttp3.Call;
import okhttp3.Request;
import okio.Timeout;

class CancellableCompletableFutureTest {

  @Test
  void shouldCancelInternalCall() {
    FakeCall fakeCall = new FakeCall();

    CompletableFuture<String> delegate = new CompletableFuture<>();
    CancellableCompletableFuture<String> future =
        CancellableCompletableFuture.of(delegate, fakeCall);

    future.cancel(true);

    assertThat(fakeCall.isCancelled()).isTrue();
    assertThat(future.isCancelled()).isTrue();
  }

  @Test
  void shouldHandleMultipleCancelsSafely() {
    FakeCall fakeCall = new FakeCall();

    CompletableFuture<String> delegate = new CompletableFuture<>();
    CancellableCompletableFuture<String> future =
        CancellableCompletableFuture.of(delegate, fakeCall);

    future.cancel(true);
    future.cancel(true);
    future.cancel(true);

    assertThat(fakeCall.isCancelled()).isTrue();
    assertThat(future.isCancelled()).isTrue();
  }

  @Test
  void shouldThrowCancellationExceptionOnGetAfterCancel() {
    FakeCall fakeCall = new FakeCall();

    CompletableFuture<String> delegate = new CompletableFuture<>();
    CancellableCompletableFuture<String> future =
        CancellableCompletableFuture.of(delegate, fakeCall);

    future.cancel(true);

    assertThatThrownBy(future::get).isInstanceOf(CancellationException.class);
  }

  @Test
  void shouldCompleteWithResultFromDelegate() {
    FakeCall fakeCall = new FakeCall();

    CompletableFuture<String> delegate = new CompletableFuture<>();
    CancellableCompletableFuture<String> future =
        CancellableCompletableFuture.of(delegate, fakeCall);

    delegate.complete("hello");

    assertThat(future.getNow("default")).isEqualTo("hello");
  }

  @Test
  void shouldPropagateExceptionFromDelegate() {
    FakeCall fakeCall = new FakeCall();

    CompletableFuture<String> delegate = new CompletableFuture<>();
    CancellableCompletableFuture<String> future =
        CancellableCompletableFuture.of(delegate, fakeCall);

    RuntimeException ex = new RuntimeException("test error");
    delegate.completeExceptionally(ex);

    assertThatThrownBy(future::get)
        .hasCauseInstanceOf(RuntimeException.class)
        .hasRootCauseMessage("test error");
  }

  @Test
  void shouldCancelWhenDelegateIsCancelled() {
    FakeCall fakeCall = new FakeCall();

    CompletableFuture<String> delegate = new CompletableFuture<>();
    CancellableCompletableFuture<String> future =
        CancellableCompletableFuture.of(delegate, fakeCall);

    delegate.cancel(true);

    assertThat(future.isCancelled()).isTrue();
    assertThat(fakeCall.isCancelled()).isTrue();
  }

  /** Minimal Call implementation for testing without Mockito. */
  private static class FakeCall implements Call {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    boolean isCancelled() {
      return cancelled.get();
    }

    @Override
    public Request request() {
      return new Request.Builder().url("http://test").build();
    }

    @Override
    public okhttp3.Response execute() {
      return null;
    }

    @Override
    public void enqueue(okhttp3.Callback callback) {}

    @Override
    public void cancel() {
      cancelled.set(true);
    }

    @Override
    public boolean isExecuted() {
      return false;
    }

    @Override
    public boolean isCanceled() {
      return cancelled.get();
    }

    @Override
    public Call clone() {
      return new FakeCall();
    }

    @Override
    public Timeout timeout() {
      return Timeout.NONE;
    }
  }
}
