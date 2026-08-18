package io.github.st2client.resource;

import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.Config;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Configs. Provides CRUD operations as well as {@code
 * updateByPackName()}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ConfigClient extends ResourceClient<Config> {

  /**
   * Constructs a new ConfigClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public ConfigClient(St2HttpClient http) {
    super(ResourceDescriptor.CONFIG, http);
  }

  /**
   * Updates a config by pack name
   *
   * @param packName the packName
   * @param instance the instance
   * @return the updated config
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public Config updateByPackName(String packName, Config instance) throws IOException {
    Objects.requireNonNull(packName, "packName must not be null");
    Objects.requireNonNull(instance, "instance must not be null");
    try (Response r = http.put(UrlPaths.join(descriptor.urlPath(), packName), instance.toMap())) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          Config.class);
    }
  }

  /**
   * Asynchronously updates a config by pack name
   *
   * @param packName the packName
   * @param instance the instance
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<Config> updateByPackNameAsync(String packName, Config instance) {
    Objects.requireNonNull(packName, "packName must not be null");
    Objects.requireNonNull(instance, "instance must not be null");
    return http.putAsync(UrlPaths.join(descriptor.urlPath(), packName), instance.toMap())
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    Config.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
