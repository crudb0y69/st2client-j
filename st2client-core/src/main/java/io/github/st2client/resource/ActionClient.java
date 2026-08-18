package io.github.st2client.resource;

import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.http.HttpStatusCodes;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.Action;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Actions. Provides CRUD operations as well as {@code
 * getEntryPoint()}, {@code clone()}, and {@code deleteAction()}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ActionClient extends ResourceClient<Action> {

  private static final String ENTRY_POINT_PATH = "views/entry_point";
  private static final String CLONE_SUFFIX = "clone";

  /**
   * Constructs a new ActionClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public ActionClient(St2HttpClient http) {
    super(ResourceDescriptor.ACTION, http);
  }

  /**
   * Retrieves the action entry point path
   *
   * @param refOrId the refOrId
   * @return the entry point path, or an empty string if the field is absent
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public String getEntryPoint(String refOrId) throws IOException {
    Objects.requireNonNull(refOrId, "refOrId must not be null");
    try (Response r =
        http.get(UrlPaths.join(descriptor.urlPath(), ENTRY_POINT_PATH, refOrId), null)) {
      Map<String, Object> data =
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
      return (String) data.getOrDefault("entry_point", "");
    }
  }

  /**
   * Asynchronously retrieves the action entry point path
   *
   * @param refOrId the refOrId
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<String> getEntryPointAsync(String refOrId) {
    Objects.requireNonNull(refOrId, "refOrId must not be null");
    return http.getAsync(UrlPaths.join(descriptor.urlPath(), ENTRY_POINT_PATH, refOrId), null)
        .thenApply(
            r -> {
              try (r) {
                Map<String, Object> data =
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
                return (String) data.getOrDefault("entry_point", "");
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, ""));
  }

  /**
   * Clones an action from a source reference
   *
   * @param sourceRef the sourceRef
   * @param destPack the destPack
   * @param destAction the destAction
   * @param overwrite the overwrite
   * @return the action
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public Action clone(String sourceRef, String destPack, String destAction, boolean overwrite)
      throws IOException {
    Objects.requireNonNull(sourceRef, "sourceRef must not be null");
    Map<String, Object> payload =
        Map.of(
            "dest_pack", destPack,
            "dest_action", destAction,
            "overwrite", overwrite);
    try (Response r =
        http.post(UrlPaths.join(descriptor.urlPath(), sourceRef, CLONE_SUFFIX), payload)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          Action.class);
    }
  }

  /**
   * Asynchronously clones an action from a source reference
   *
   * @param sourceRef the sourceRef
   * @param destPack the destPack
   * @param destAction the destAction
   * @param overwrite the overwrite
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<Action> cloneAsync(
      String sourceRef, String destPack, String destAction, boolean overwrite) {
    Objects.requireNonNull(sourceRef, "sourceRef must not be null");
    Map<String, Object> payload =
        Map.of("dest_pack", destPack, "dest_action", destAction, "overwrite", overwrite);
    return http.postAsync(UrlPaths.join(descriptor.urlPath(), sourceRef, CLONE_SUFFIX), payload)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    Action.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public boolean deleteAction(String refOrId, boolean removeFiles) throws IOException {
    Objects.requireNonNull(refOrId, "refOrId must not be null");
    try (Response r = http.delete(deleteActionUrl(refOrId, removeFiles))) {
      return r.code() == HttpStatusCodes.OK.getCode()
          || r.code() == HttpStatusCodes.NO_CONTENT.getCode();
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) return false;
      throw e;
    }
  }

  public CompletableFuture<Boolean> deleteActionAsync(String refOrId, boolean removeFiles) {
    Objects.requireNonNull(refOrId, "refOrId must not be null");
    return http.deleteAsync(deleteActionUrl(refOrId, removeFiles))
        .thenApply(
            r -> {
              try (r) {
                return r.code() == HttpStatusCodes.OK.getCode()
                    || r.code() == HttpStatusCodes.NO_CONTENT.getCode();
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, false));
  }

  private String deleteActionUrl(String refOrId, boolean removeFiles) {
    return resourceUrl(refOrId) + "?remove_files=" + removeFiles;
  }
}
