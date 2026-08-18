package io.github.st2client.resource;

import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.Execution;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Executions. Adds execution-specific operations: re-run, pause,
 * resume, cancel, output/result retrieval, child execution listing, one-shot execute, and status
 * monitoring via a shared scheduler.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ExecutionClient extends ResourceClient<Execution> {

  private static final Logger log = LoggerFactory.getLogger(ExecutionClient.class);
  private static final Set<String> TERMINAL_STATUSES =
      Set.of("succeeded", "failed", "timeout", "canceled");
  private static final long DEFAULT_POLL_INTERVAL_MS = 2000;
  private static final long DEFAULT_TIMEOUT_MS = 30 * 60 * 1000;

  private final ScheduledExecutorService scheduler =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "st2-status-pool");
            t.setDaemon(true);
            return t;
          });
  private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
  private final AtomicBoolean closed = new AtomicBoolean(false);

  public ExecutionClient(St2HttpClient http) {
    super(ResourceDescriptor.EXECUTION, http);
  }

  public Execution reRun(
      String executionId,
      Map<String, Object> parameters,
      List<String> tasks,
      List<String> noReset,
      int delay)
      throws IOException {
    Map<String, Object> payload = buildReRunPayload(parameters, tasks, noReset, delay);
    try (Response r =
        http.post(UrlPaths.join(descriptor.urlPath(), executionId, "re_run"), payload)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          Execution.class);
    }
  }

  public CompletableFuture<Execution> reRunAsync(
      String executionId,
      Map<String, Object> parameters,
      List<String> tasks,
      List<String> noReset,
      int delay) {
    Map<String, Object> payload = buildReRunPayload(parameters, tasks, noReset, delay);
    return http.postAsync(UrlPaths.join(descriptor.urlPath(), executionId, "re_run"), payload)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    Execution.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  private static Map<String, Object> buildReRunPayload(
      Map<String, Object> parameters, List<String> tasks, List<String> noReset, int delay) {
    List<String> resetList = new ArrayList<>(tasks != null ? tasks : Collections.emptyList());
    if (noReset != null) resetList.removeAll(noReset);
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("parameters", parameters != null ? parameters : Collections.emptyMap());
    payload.put("tasks", tasks != null ? tasks : Collections.emptyList());
    payload.put("reset", resetList);
    payload.put("delay", delay);
    return payload;
  }

  public String getOutput(String executionId, String outputType) throws IOException {
    String path = UrlPaths.join(descriptor.urlPath(), executionId, "output");
    Map<String, String> params = outputType != null ? Map.of("output_type", outputType) : null;
    try (Response r = http.get(path, params)) {
      return r.body() != null ? r.body().string() : "";
    }
  }

  public CompletableFuture<String> getOutputAsync(String executionId, String outputType) {
    String path = UrlPaths.join(descriptor.urlPath(), executionId, "output");
    Map<String, String> params = outputType != null ? Map.of("output_type", outputType) : null;
    return http.getAsync(path, params)
        .thenApply(
            r -> {
              try (r) {
                return r.body() != null ? r.body().string() : "";
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public String getResult(String executionId) throws IOException {
    try (Response r = http.get(UrlPaths.join(descriptor.urlPath(), executionId, "result"), null)) {
      return r.body() != null ? r.body().string() : "";
    }
  }

  public CompletableFuture<String> getResultAsync(String executionId) {
    return http.getAsync(UrlPaths.join(descriptor.urlPath(), executionId, "result"), null)
        .thenApply(
            r -> {
              try (r) {
                return r.body() != null ? r.body().string() : "";
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public Execution pause(String executionId) throws IOException {
    return setStatus(executionId, "pausing");
  }

  public CompletableFuture<Execution> pauseAsync(String executionId) {
    return setStatusAsync(executionId, "pausing");
  }

  public Execution resume(String executionId) throws IOException {
    return setStatus(executionId, "resuming");
  }

  public CompletableFuture<Execution> resumeAsync(String executionId) {
    return setStatusAsync(executionId, "resuming");
  }

  public Execution cancel(String executionId) throws IOException {
    return setStatus(executionId, "canceling");
  }

  public CompletableFuture<Execution> cancelAsync(String executionId) {
    return setStatusAsync(executionId, "canceling");
  }

  private Execution setStatus(String executionId, String status) throws IOException {
    try (Response r =
        http.put(UrlPaths.join(descriptor.urlPath(), executionId), Map.of("status", status))) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          Execution.class);
    }
  }

  private CompletableFuture<Execution> setStatusAsync(String executionId, String status) {
    return http.putAsync(UrlPaths.join(descriptor.urlPath(), executionId), Map.of("status", status))
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    Execution.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public CompletableFuture<Execution> awaitCompletion(
      String executionId, long pollIntervalMs, long timeoutMs) {
    java.util.Objects.requireNonNull(executionId, "executionId must not be null");
    CompletableFuture<Execution> future = new CompletableFuture<>();

    long deadline = System.currentTimeMillis() + timeoutMs;

    Runnable poll =
        () -> {
          if (future.isDone()) return;
          if (System.currentTimeMillis() >= deadline) {
            future.completeExceptionally(
                new TimeoutException(
                    "Execution " + executionId + " did not complete within " + timeoutMs + "ms"));
            return;
          }
          try {
            Execution exec = getById(executionId);
            if (exec != null && TERMINAL_STATUSES.contains(exec.getStatus())) {
              future.complete(exec);
            }
          } catch (Exception e) {
            future.completeExceptionally(e);
          }
        };

    ScheduledFuture<?> scheduledTask =
        scheduler.scheduleWithFixedDelay(
            poll, pollIntervalMs, pollIntervalMs, TimeUnit.MILLISECONDS);
    future.whenComplete((result, ex) -> scheduledTask.cancel(false));
    return future;
  }

  public CompletableFuture<Execution> awaitCompletion(String executionId) {
    return awaitCompletion(executionId, DEFAULT_POLL_INTERVAL_MS, DEFAULT_TIMEOUT_MS);
  }

  public List<Execution> getChildren(String executionId, int depth) throws IOException {
    Map<String, String> params = depth >= 0 ? Map.of("depth", String.valueOf(depth)) : null;
    try (Response r =
        http.get(UrlPaths.join(descriptor.urlPath(), executionId, "children"), params)) {
      List<Map<String, Object>> items =
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_LIST_TYPE);
      return deserializeList(items);
    }
  }

  public CompletableFuture<List<Execution>> getChildrenAsync(String executionId, int depth) {
    Map<String, String> params = depth >= 0 ? Map.of("depth", String.valueOf(depth)) : null;
    return http.getAsync(UrlPaths.join(descriptor.urlPath(), executionId, "children"), params)
        .thenApply(
            r -> {
              try (r) {
                List<Map<String, Object>> items =
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_LIST_TYPE);
                return deserializeList(items);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public Execution execute(String actionRef, Map<String, Object> parameters) throws IOException {
    java.util.Objects.requireNonNull(actionRef, "actionRef must not be null");
    java.util.Objects.requireNonNull(parameters, "parameters must not be null");
    Execution exec = new Execution();
    exec.setAction(Map.of("ref", actionRef));
    exec.setParameters(parameters);
    return create(exec);
  }

  public Map<String, Object> inspect(String workflowDefinition) throws IOException {
    java.util.Objects.requireNonNull(workflowDefinition, "workflowDefinition must not be null");
    try (Response r =
        http.postPlainText(UrlPaths.join("workflows", "inspect"), workflowDefinition)) {
      String body = r.body() != null ? r.body().string() : "{}";
      return Resource.readJson(body, Resource.MAP_TYPE);
    }
  }

  public CompletableFuture<Map<String, Object>> inspectAsync(String workflowDefinition) {
    java.util.Objects.requireNonNull(workflowDefinition, "workflowDefinition must not be null");
    return http.postPlainTextAsync(UrlPaths.join("workflows", "inspect"), workflowDefinition)
        .thenApply(
            r -> {
              try (r) {
                String body = r.body() != null ? r.body().string() : "{}";
                return Resource.readJson(body, Resource.MAP_TYPE);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public ScheduledFuture<?> onStatusChange(
      String executionId, Consumer<Execution> callback, long pollIntervalMs) {
    java.util.Objects.requireNonNull(executionId, "executionId must not be null");
    java.util.Objects.requireNonNull(callback, "callback must not be null");
    if (closed.get()) {
      throw new IllegalStateException("Execution status monitors have been shut down");
    }

    ScheduledFuture<?> existing = scheduledTasks.remove(executionId);
    if (existing != null && !existing.isDone()) {
      existing.cancel(true);
    }

    final String[] lastStatus = {null};

    Runnable poll =
        () -> {
          try {
            Execution exec = getById(executionId);
            if (exec != null
                && exec.getStatus() != null
                && !exec.getStatus().equals(lastStatus[0])) {
              lastStatus[0] = exec.getStatus();
              callback.accept(exec);
              if (TERMINAL_STATUSES.contains(exec.getStatus())) {
                cancelStatusMonitor(executionId, null);
              }
            }
          } catch (Exception e) {
            log.warn("Error polling execution status for {}: {}", executionId, e.getMessage());
          }
        };

    ScheduledFuture<?> future =
        scheduler.scheduleWithFixedDelay(
            poll, pollIntervalMs, pollIntervalMs, TimeUnit.MILLISECONDS);
    scheduledTasks.put(executionId, future);
    return future;
  }

  public void cancelStatusMonitor(String executionId, ScheduledFuture<?> future) {
    ScheduledFuture<?> tracked = scheduledTasks.remove(executionId);
    if (future != null) {
      future.cancel(true);
    }
    if (tracked != null && tracked != future) {
      tracked.cancel(true);
    }
  }

  public void shutdownStatusMonitors() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }
    scheduledTasks.forEach((id, future) -> future.cancel(true));
    scheduledTasks.clear();
    scheduler.shutdown();
  }
}
