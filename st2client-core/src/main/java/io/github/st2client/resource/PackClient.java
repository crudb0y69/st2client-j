package io.github.st2client.resource;

import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.Pack;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Packs. Adds pack-specific operations: install, remove
 * (uninstall), search, and register.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class PackClient extends ResourceClient<Pack> {

  /**
   * Constructs a new PackClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public PackClient(St2HttpClient http) {
    super(ResourceDescriptor.PACK, http);
  }

  /**
   * Installs packs
   *
   * @param packs the packs
   * @param force the force
   * @param skipDependencies the skipDependencies
   * @return the server response data
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public Map<String, Object> install(List<String> packs, boolean force, boolean skipDependencies)
      throws IOException {
    Map<String, Object> payload =
        Map.of(
            "packs", packs,
            "force", force,
            "skip_dependencies", skipDependencies);
    try (Response r = http.post(ResourceDescriptor.PACK_INSTALL_PATH, payload)) {
      return Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
    }
  }

  public CompletableFuture<Map<String, Object>> installAsync(
      List<String> packs, boolean force, boolean skipDeps) {
    Map<String, Object> payload =
        Map.of("packs", packs, "force", force, "skip_dependencies", skipDeps);
    return http.postAsync(ResourceDescriptor.PACK_INSTALL_PATH, payload)
        .thenApply(
            r -> {
              try (r) {
                return Resource.readJson(
                    r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public Map<String, Object> remove(List<String> packs) throws IOException {
    try (Response r = http.post(ResourceDescriptor.PACK_UNINSTALL_PATH, Map.of("packs", packs))) {
      return Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
    }
  }

  public CompletableFuture<Map<String, Object>> removeAsync(List<String> packs) {
    return http.postAsync(ResourceDescriptor.PACK_UNINSTALL_PATH, Map.of("packs", packs))
        .thenApply(
            r -> {
              try (r) {
                return Resource.readJson(
                    r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public List<Pack> search(String query) throws IOException {
    try (Response r = http.post(ResourceDescriptor.PACK_SEARCH_PATH, Map.of("query", query))) {
      List<Map<String, Object>> items =
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_LIST_TYPE);
      return deserializeList(items);
    }
  }

  public CompletableFuture<List<Pack>> searchAsync(String query) {
    return http.postAsync(ResourceDescriptor.PACK_SEARCH_PATH, Map.of("query", query))
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

  public Pack register(List<String> packs, List<String> types) throws IOException {
    Map<String, Object> payload = new LinkedHashMap<>();
    if (packs != null) payload.put("packs", packs);
    if (types != null) payload.put("types", types);
    try (Response r = http.post(ResourceDescriptor.PACK_REGISTER_PATH, payload)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          Pack.class);
    }
  }

  public CompletableFuture<Pack> registerAsync(List<String> packs, List<String> types) {
    Map<String, Object> payload = new LinkedHashMap<>();
    if (packs != null) payload.put("packs", packs);
    if (types != null) payload.put("types", types);
    return http.postAsync(ResourceDescriptor.PACK_REGISTER_PATH, payload)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    Pack.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
