package io.github.st2client.internal.http;

import io.github.st2client.config.RetryPolicy;
import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.CancellableCompletableFuture;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.*;

/**
 * Lightweight HTTP client wrapping a shared OkHttpClient for StackStorm API interactions. Each
 * instance targets one API endpoint root URL. Authentication is handled by the OkHttpClient
 * interceptor chain via {@link AuthHeadersInterceptor}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class St2HttpClient {

  private static final Logger log = LoggerFactory.getLogger(St2HttpClient.class);
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private static final MediaType TEXT_PLAIN = MediaType.get("text/plain; charset=utf-8");
  private static final int MAX_ERROR_BODY_BYTES = 16384;

  private final String root;
  private final OkHttpClient okHttp;
  private final RetryPolicy retryPolicy;

  /**
   * Creates an St2HttpClient targeting the given API root URL.
   *
   * @param root the API endpoint root URL (e.g. http://host:9101/v1)
   * @param okHttp the shared OkHttpClient with interceptors configured
   */
  public St2HttpClient(String root, OkHttpClient okHttp) {
    this(root, okHttp, null);
  }

  /**
   * Creates an St2HttpClient with an optional retry policy for transient failures.
   *
   * @param root the API endpoint root URL (e.g. http://host:9101/v1)
   * @param okHttp the shared OkHttpClient with interceptors configured
   * @param retryPolicy the retry policy, or null to disable retries
   * @since 0.1.0
   */
  public St2HttpClient(String root, OkHttpClient okHttp, RetryPolicy retryPolicy) {
    this.root = UrlPaths.stripTrailingSlash(root);
    this.okHttp = okHttp;
    this.retryPolicy = retryPolicy;
  }

  /**
   * Returns the API endpoint root URL.
   *
   * @return the root URL
   */
  public String root() {
    return root;
  }

  /**
   * Sends a synchronous GET request with optional query parameters.
   *
   * @param path the API path relative to the root URL
   * @param params optional query parameters (may be null)
   * @return the HTTP response with a buffered body
   * @throws IOException if an I/O error occurs
   */
  public Response get(String path, Map<String, String> params) throws IOException {
    HttpUrl.Builder b = HttpUrl.get(root + path).newBuilder();
    if (params != null) params.forEach(b::addQueryParameter);
    return execute(new Request.Builder().url(b.build()).get().build());
  }

  /**
   * Sends a synchronous POST request with an optional JSON body.
   *
   * @param path the API path relative to the root URL
   * @param body the request body object (serialized to JSON), may be null
   * @return the HTTP response with a buffered body
   * @throws IOException if an I/O error occurs
   */
  public Response post(String path, Object body) throws IOException {
    RequestBody rb =
        body != null
            ? RequestBody.create(Resource.writeJson(body), JSON)
            : RequestBody.create("", JSON);
    return execute(new Request.Builder().url(root + path).post(rb).build());
  }

  /**
   * Sends a synchronous POST request with a {@code text/plain} body.
   *
   * @param path the API path relative to the root URL
   * @param body the raw text body
   * @return the HTTP response with a buffered body
   * @throws IOException if an I/O error occurs
   * @since 0.1.0
   */
  public Response postPlainText(String path, String body) throws IOException {
    RequestBody rb = RequestBody.create(body, TEXT_PLAIN);
    return execute(new Request.Builder().url(root + path).post(rb).build());
  }

  /**
   * Sends a synchronous PUT request with an optional JSON body.
   *
   * @param path the API path relative to the root URL
   * @param body the request body object (serialized to JSON), may be null
   * @return the HTTP response with a buffered body
   * @throws IOException if an I/O error occurs
   */
  public Response put(String path, Object body) throws IOException {
    RequestBody rb =
        body != null
            ? RequestBody.create(Resource.writeJson(body), JSON)
            : RequestBody.create("", JSON);
    return execute(new Request.Builder().url(root + path).put(rb).build());
  }

  /**
   * Sends a synchronous DELETE request.
   *
   * @param path the API path relative to the root URL
   * @return the HTTP response with a buffered body
   * @throws IOException if an I/O error occurs
   */
  public Response delete(String path) throws IOException {
    return execute(new Request.Builder().url(root + path).delete().build());
  }

  /**
   * Sends an asynchronous GET request with optional query parameters.
   *
   * @param path the API path relative to the root URL
   * @param params optional query parameters (may be null)
   * @return a CompletableFuture that completes with the buffered response
   */
  public CompletableFuture<Response> getAsync(String path, Map<String, String> params) {
    HttpUrl.Builder b = HttpUrl.get(root + path).newBuilder();
    if (params != null) params.forEach(b::addQueryParameter);
    return executeAsync(new Request.Builder().url(b.build()).get().build());
  }

  /**
   * Sends an asynchronous POST request with an optional JSON body.
   *
   * @param path the API path relative to the root URL
   * @param body the request body object (serialized to JSON), may be null
   * @return a CompletableFuture that completes with the buffered response
   */
  public CompletableFuture<Response> postAsync(String path, Object body) {
    try {
      RequestBody rb =
          body != null
              ? RequestBody.create(Resource.writeJson(body), JSON)
              : RequestBody.create("", JSON);
      return executeAsync(new Request.Builder().url(root + path).post(rb).build());
    } catch (Exception e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  /**
   * Sends an asynchronous POST request with a {@code text/plain} body.
   *
   * @param path the API path relative to the root URL
   * @param body the raw text body
   * @return a CompletableFuture that completes with the buffered response
   * @since 0.1.0
   */
  public CompletableFuture<Response> postPlainTextAsync(String path, String body) {
    try {
      RequestBody rb = RequestBody.create(body, TEXT_PLAIN);
      return executeAsync(new Request.Builder().url(root + path).post(rb).build());
    } catch (Exception e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  /**
   * Sends an asynchronous PUT request with an optional JSON body.
   *
   * @param path the API path relative to the root URL
   * @param body the request body object (serialized to JSON), may be null
   * @return a CompletableFuture that completes with the buffered response
   */
  public CompletableFuture<Response> putAsync(String path, Object body) {
    try {
      RequestBody rb =
          body != null
              ? RequestBody.create(Resource.writeJson(body), JSON)
              : RequestBody.create("", JSON);
      return executeAsync(new Request.Builder().url(root + path).put(rb).build());
    } catch (Exception e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  /**
   * Sends an asynchronous DELETE request.
   *
   * @param path the API path relative to the root URL
   * @return a CompletableFuture that completes with the buffered response
   */
  public CompletableFuture<Response> deleteAsync(String path) {
    return executeAsync(new Request.Builder().url(root + path).delete().build());
  }

  /**
   * Executes a synchronous HTTP request and buffers the response body. Throws an {@link
   * OperationFailureException} for non-successful responses.
   *
   * @param request the HTTP request to execute
   * @return the buffered HTTP response
   * @throws IOException if an I/O error occurs
   */
  private Response execute(Request request) throws IOException {
    int attempt = 0;
    while (true) {
      Response response = okHttp.newCall(request).execute();
      try {
        if (response.isSuccessful()) {
          ResponseBody body = response.body();
          String bodyStr = body != null ? body.string() : "";
          Response buffered =
              response
                  .newBuilder()
                  .body(ResponseBody.create(bodyStr, body != null ? body.contentType() : JSON))
                  .build();
          response.close();
          return buffered;
        }
        // Unsuccessful - retry if policy allows
        if (retryPolicy != null
            && attempt < retryPolicy.getMaxRetries()
            && isRetryableMethod(request)
            && retryPolicy.getRetryOnStatus().contains(response.code())) {
          attempt++;
          long delay = retryPolicy.calculateDelay(attempt);
          log.debug(
              "Retry attempt {} for {} {} after {}ms (status={})",
              attempt,
              request.method(),
              request.url(),
              delay,
              response.code());
          response.close();
          sleep(delay);
          continue;
        }
        throw handleError(response);
      } catch (IOException e) {
        response.close();
        if (retryPolicy != null
            && attempt < retryPolicy.getMaxRetries()
            && isRetryableMethod(request)) {
          attempt++;
          long delay = retryPolicy.calculateDelay(attempt);
          log.debug(
              "Retry attempt {} for {} {} after {}ms (I/O error)",
              attempt,
              request.method(),
              request.url(),
              delay);
          sleep(delay);
          continue;
        }
        throw e;
      }
    }
  }

  /**
   * Executes an asynchronous HTTP request and buffers the response body.
   *
   * @param request the HTTP request to execute
   * @return a CompletableFuture that completes with the buffered response, or exceptionally with an
   *     IOException or OperationFailureException
   */
  private CompletableFuture<Response> executeAsync(Request request) {
    return executeAsync(request, 0);
  }

  private CompletableFuture<Response> executeAsync(Request request, int attempt) {
    Call call = okHttp.newCall(request);
    CancellableCompletableFuture<Response> future = CancellableCompletableFuture.create(call);
    call.enqueue(
        new Callback() {
          @Override
          public void onFailure(@NotNull Call failedCall, @NotNull IOException e) {
            if (future.isCancelled()) {
              return;
            }
            if (shouldRetry(request, attempt, -1)) {
              retryAsync(request, attempt, future);
              return;
            }
            future.completeExceptionally(e);
          }

          @Override
          public void onResponse(@NotNull Call respondedCall, @NotNull Response response) {
            if (future.isCancelled()) {
              response.close();
              return;
            }
            if (!response.isSuccessful()) {
              int status = response.code();
              if (shouldRetry(request, attempt, status)) {
                response.close();
                retryAsync(request, attempt, future);
                return;
              }
              future.completeExceptionally(handleError(response));
              return;
            }
            try {
              ResponseBody body = response.body();
              String bodyStr = body != null ? body.string() : "";
              Response buffered =
                  response
                      .newBuilder()
                      .body(ResponseBody.create(bodyStr, body != null ? body.contentType() : JSON))
                      .build();
              future.complete(buffered);
            } catch (Exception e) {
              future.completeExceptionally(e);
            }
          }
        });
    return future;
  }

  private boolean shouldRetry(Request request, int attempt, int statusCode) {
    if (retryPolicy == null
        || attempt >= retryPolicy.getMaxRetries()
        || !isRetryableMethod(request)) {
      return false;
    }
    return statusCode < 0 || retryPolicy.getRetryOnStatus().contains(statusCode);
  }

  private void retryAsync(Request request, int attempt, CompletableFuture<Response> future) {
    if (future.isCancelled()) {
      return;
    }
    int nextAttempt = attempt + 1;
    long delay = retryPolicy.calculateDelay(nextAttempt);
    log.debug(
        "Retry attempt {} for {} {} after {}ms",
        nextAttempt,
        request.method(),
        request.url(),
        delay);
    CompletableFuture.delayedExecutor(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
        .execute(
            () ->
                executeAsync(request, nextAttempt)
                    .whenComplete(
                        (response, error) -> {
                          if (error != null) {
                            future.completeExceptionally(error);
                          } else {
                            future.complete(response);
                          }
                        }));
  }

  /**
   * Extracts a fault string from an error response body and wraps it in an {@link
   * OperationFailureException}.
   *
   * @param response the error HTTP response
   * @return an OperationFailureException with the parsed fault string
   */
  private OperationFailureException handleError(Response response) {
    String faultString = null;
    try {
      if (response.body() != null) {
        byte[] bytes = new byte[MAX_ERROR_BODY_BYTES];
        int read = response.body().source().read(bytes);
        String body = new String(bytes, 0, Math.max(read, 0), StandardCharsets.UTF_8);
        try {
          Map<String, Object> content = Resource.readJson(body, Resource.MAP_TYPE);
          faultString = (String) content.get("faultstring");
        } catch (Exception e) {
          if (read > 0) {
            faultString = read >= MAX_ERROR_BODY_BYTES ? body + "...(truncated)" : body;
          }
        }
      }
    } catch (IOException | RuntimeException ignored) {
    }
    String message = faultString != null ? faultString : response.message();
    if (response.body() != null) response.close();
    return new OperationFailureException(response.code(), message);
  }

  /** Checks whether the HTTP method is idempotent and therefore safe to retry. */
  private static boolean isRetryableMethod(Request request) {
    return Set.of("GET", "PUT", "DELETE", "HEAD", "OPTIONS").contains(request.method());
  }

  /** Sleeps for the specified duration, handling interruption. */
  private static void sleep(long ms) throws IOException {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Retry interrupted", e);
    }
  }
}
