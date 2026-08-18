package io.github.st2client.resource;

import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.Resource;
import io.github.st2client.model.TriggerInstance;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Trigger Instances. Provides CRUD operations as well as {@code
 * reemit()} and {@code reemitAsync()}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class TriggerInstanceClient extends ResourceClient<TriggerInstance> {

  /**
   * Constructs a new TriggerInstanceClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public TriggerInstanceClient(St2HttpClient http) {
    super(ResourceDescriptor.TRIGGER_INSTANCE, http);
  }

  /**
   * Re-emits a trigger instance
   *
   * @param triggerInstanceId the triggerInstanceId
   * @return the trigger instance
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public TriggerInstance reemit(String triggerInstanceId) throws IOException {
    try (Response r =
        http.post(UrlPaths.join("triggerinstances", triggerInstanceId, "re_emit"), Map.of())) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          TriggerInstance.class);
    }
  }

  /**
   * Asynchronously re-emits a trigger instance
   *
   * @param triggerInstanceId the triggerInstanceId
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<TriggerInstance> reemitAsync(String triggerInstanceId) {
    return http.postAsync(UrlPaths.join("triggerinstances", triggerInstanceId, "re_emit"), Map.of())
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    TriggerInstance.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
