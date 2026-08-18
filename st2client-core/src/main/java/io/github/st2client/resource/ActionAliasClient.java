package io.github.st2client.resource;

import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.ActionAlias;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Action Aliases. Provides CRUD operations as well as {@code
 * match()} and {@code matchAsync()}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ActionAliasClient extends ResourceClient<ActionAlias> {

  /**
   * Constructs a new ActionAliasClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public ActionAliasClient(St2HttpClient http) {
    super(ResourceDescriptor.ACTION_ALIAS, http);
  }

  /**
   * Matches an action alias
   *
   * @param instance the instance
   * @return the matched action alias
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public ActionAlias match(ActionAlias instance) throws IOException {
    try (Response r = http.post(UrlPaths.join("actionalias", "match"), instance.toMap())) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          ActionAlias.class);
    }
  }

  /**
   * Asynchronously matches an action alias
   *
   * @param instance the instance
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<ActionAlias> matchAsync(ActionAlias instance) {
    return http.postAsync(UrlPaths.join("actionalias", "match"), instance.toMap())
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    ActionAlias.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
