package io.github.st2client.internal;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;

/**
 * A {@link CompletableFuture} that cancels the underlying OkHttp {@link Call} when the future is
 * cancelled.
 *
 * @param <T> the result type
 * @author crudb0y69
 * @since 0.1.0
 */
public class CancellableCompletableFuture<T> extends CompletableFuture<T> {

  private final Call call;

  private CancellableCompletableFuture(Call call) {
    this.call = call;
  }

  private CancellableCompletableFuture(CompletableFuture<T> delegate, Call call) {
    this.call = call;
    delegate.whenComplete(
        (result, ex) -> {
          if (ex instanceof CancellationException) {
            cancel(true);
          } else if (ex != null) {
            completeExceptionally(ex);
          } else {
            complete(result);
          }
        });
  }

  /**
   * Creates a future bound to an OkHttp call that has not completed yet.
   *
   * @param call the in-flight call
   * @return a future that cancels {@code call} when cancelled
   */
  public static <T> CancellableCompletableFuture<T> create(Call call) {
    return new CancellableCompletableFuture<>(call);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    if (call != null) {
      call.cancel();
    }
    return super.cancel(mayInterruptIfRunning);
  }

  public static <T> CancellableCompletableFuture<T> of(CompletableFuture<T> delegate, Call call) {
    return new CancellableCompletableFuture<>(delegate, call);
  }
}
