package io.github.st2client.resource;

import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.http.HttpStatusCodes;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Response;

/**
 * Generic CRUD resource client. Uses {@link ResourceDescriptor} for URL path resolution without
 * reflection.
 *
 * @param <T> the resource model type
 * @author crudb0y69
 * @since 0.1.0
 */
public class ResourceClient<T extends Resource> implements ResourceReader<T>, ResourceWriter<T> {

  private static final Logger log = LoggerFactory.getLogger(ResourceClient.class);
  static final int DEFAULT_PAGE_SIZE = 100;

  protected final ResourceDescriptor<T> descriptor;
  protected final St2HttpClient http;

  /**
   * Constructs a new ResourceClient.
   *
   * @param descriptor the descriptor
   * @param http the http
   * @since 0.1.0
   */
  public ResourceClient(ResourceDescriptor<T> descriptor, St2HttpClient http) {
    this.descriptor = descriptor;
    this.http = http;
  }

  /**
   * Retrieves all resources
   *
   * @return the list of resources
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public List<T> getAll() throws IOException {
    return getAll(null);
  }

  /**
   * Retrieves all resources
   *
   * @param params the params
   * @return the list of resources
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public List<T> getAll(Map<String, String> params) throws IOException {
    int pageSize = DEFAULT_PAGE_SIZE;
    Map<String, String> filters = new LinkedHashMap<>();
    if (params != null) {
      for (Map.Entry<String, String> entry : params.entrySet()) {
        if ("limit".equals(entry.getKey())) {
          pageSize = Integer.parseInt(entry.getValue());
        } else if (!"offset".equals(entry.getKey())) {
          filters.put(entry.getKey(), entry.getValue());
        }
      }
    }
    if (pageSize <= 0) {
      pageSize = DEFAULT_PAGE_SIZE;
    }
    List<T> all = new ArrayList<>();
    int offset = 0;
    while (true) {
      Map<String, String> pageParams = new LinkedHashMap<>(filters);
      pageParams.put("limit", String.valueOf(pageSize));
      pageParams.put("offset", String.valueOf(offset));
      QueryResult<T> page = queryWithCount(pageParams);
      List<T> items = page.items() != null ? page.items() : List.of();
      all.addAll(items);
      if (items.size() < pageSize) {
        break;
      }
      if (page.totalCount() > 0 && all.size() >= page.totalCount()) {
        break;
      }
      offset += items.size();
    }
    return all;
  }

  /**
   * Lazily pages through resources using {@link #queryWithCount(Map)}.
   *
   * @param filters optional query filters (may be null)
   * @param pageSize number of items per page
   * @return an iterator over matching resources
   * @since 0.1.0
   */
  public PagedIterator<T> iterate(Map<String, String> filters, int pageSize) {
    return new PagedIterator<>(
        pageParams -> {
          Map<String, String> merged = new LinkedHashMap<>();
          if (filters != null) {
            merged.putAll(filters);
          }
          merged.putAll(pageParams);
          try {
            return queryWithCount(merged);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        },
        pageSize);
  }

  /**
   * Retrieves a resource by its ID. Returns {@code null} if the resource is not found (HTTP 404).
   *
   * @param id the id
   * @return the resource, or {@code null} if not found
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public T getById(String id) throws IOException {
    try (Response r = http.get(resourceUrl(id), null)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          descriptor.resourceClass());
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) return null;
      throw e;
    }
  }

  /**
   * Retrieves a resource by its name. Returns {@code null} if no matching resource is found.
   *
   * @param name the name
   * @return the resource, or {@code null} if not found
   * @throws OperationFailureException if more than one resource matches the name
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public T getByName(String name) throws IOException {
    List<T> results = query(Map.of("name", name));
    if (results.isEmpty()) return null;
    if (results.size() > 1) {
      throw new OperationFailureException(
          "More than one "
              + descriptor.typeName().toLowerCase()
              + " named \""
              + name
              + "\" found.");
    }
    return results.get(0);
  }

  /**
   * Asynchronously retrieves a resource by its name.
   *
   * @param name the resource name
   * @return a CompletableFuture completing with the resource, or {@code null} if not found
   * @since 0.1.0
   */
  public CompletableFuture<T> getByNameAsync(String name) {
    return http.getAsync(listUrl(), Map.of("name", name))
        .thenApply(
            r -> {
              try (r) {
                List<Map<String, Object>> items =
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_LIST_TYPE);
                List<T> results = deserializeList(items);
                if (results.isEmpty()) return null;
                if (results.size() > 1) {
                  throw new OperationFailureException(
                      "More than one "
                          + descriptor.typeName().toLowerCase()
                          + " named \""
                          + name
                          + "\" found.");
                }
                return results.get(0);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, null));
  }

  /**
   * Retrieves a resource by its reference or ID
   *
   * @param refOrId the refOrId
   * @return the resource
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public T getByRefOrId(String refOrId) throws IOException {
    return getById(refOrId);
  }

  /**
   * Asynchronously retrieves a resource by its reference or ID.
   *
   * @param refOrId the reference or ID
   * @return a CompletableFuture completing with the resource, or {@code null} if not found
   * @since 0.1.0
   */
  public CompletableFuture<T> getByRefOrIdAsync(String refOrId) {
    return getByIdAsync(refOrId);
  }

  /**
   * Retrieves a resource property. Returns {@code null} if the resource is not found (HTTP 404).
   *
   * @param id the id
   * @param propertyName the propertyName
   * @return the resource, or {@code null} if not found
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public T getProperty(String id, String propertyName) throws IOException {
    try (Response r = http.get(propertyUrl(id, propertyName), null)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          descriptor.resourceClass());
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) return null;
      throw e;
    }
  }

  /**
   * Asynchronously retrieves a resource property.
   *
   * @param id the resource ID
   * @param propertyName the property name
   * @return a CompletableFuture completing with the resource, or {@code null} if not found
   * @since 0.1.0
   */
  public CompletableFuture<T> getPropertyAsync(String id, String propertyName) {
    return http.getAsync(propertyUrl(id, propertyName), null)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    descriptor.resourceClass());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, null));
  }

  /**
   * Queries resources with the given filter parameters. Returns an empty list if the endpoint
   * returns HTTP 404 (e.g. when the collection does not exist).
   *
   * @param params the params
   * @return the list of resources (empty if not found)
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public List<T> query(Map<String, String> params) throws IOException {
    try (Response r = http.get(listUrl(), params)) {
      List<Map<String, Object>> items =
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_LIST_TYPE);
      return deserializeList(items);
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) return Collections.emptyList();
      throw e;
    }
  }

  /**
   * Queries resources with total count from the X-Total-Count header. Returns an empty {@link
   * QueryResult} if the endpoint returns HTTP 404.
   *
   * @param params the params
   * @return a QueryResult containing the matching resources and total count
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public QueryResult<T> queryWithCount(Map<String, String> params) throws IOException {
    try (Response r = http.get(listUrl(), params)) {
      List<Map<String, Object>> items =
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_LIST_TYPE);
      return new QueryResult<>(
          deserializeList(items), Integer.parseInt(r.header("X-Total-Count", "0")));
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode())
        return new QueryResult<>(Collections.emptyList(), 0);
      throw e;
    }
  }

  /**
   * Creates a new resource
   *
   * @param instance the instance
   * @return the resource
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public T create(T instance) throws IOException {
    try (Response r = http.post(listUrl(), instance.toMap())) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          descriptor.resourceClass());
    }
  }

  /**
   * Updates an existing resource
   *
   * @param instance the instance
   * @return the resource
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public T update(T instance) throws IOException {
    try (Response r = http.put(resourceUrl(requireResourceKey(instance)), instance.toMap())) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          descriptor.resourceClass());
    }
  }

  /**
   * Deletes a resource by its ID. Returns {@code false} if the resource is not found (HTTP 404).
   *
   * @param id the id
   * @return {@code true} if deleted, {@code false} if the resource was not found
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public boolean deleteById(String id) throws IOException {
    try (Response r = http.delete(resourceUrl(id))) {
      return r.code() == HttpStatusCodes.OK.getCode()
          || r.code() == HttpStatusCodes.NO_CONTENT.getCode();
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) return false;
      throw e;
    }
  }

  /**
   * Deletes the given resource. Returns {@code false} if the resource is not found (HTTP 404).
   *
   * @param instance the instance
   * @return {@code true} if deleted, {@code false} if the resource was not found
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public boolean delete(T instance) throws IOException {
    return deleteById(requireResourceKey(instance));
  }

  /**
   * Asynchronously deletes the given resource.
   *
   * @param instance the resource to delete
   * @return a CompletableFuture completing with {@code true} if deleted, {@code false} if not found
   * @since 0.1.0
   */
  public CompletableFuture<Boolean> deleteAsync(T instance) {
    return deleteByIdAsync(requireResourceKey(instance));
  }

  /**
   * Asynchronously retrieves all resources
   *
   * @param params the params
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<List<T>> getAllAsync(Map<String, String> params) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return getAll(params);
          } catch (RuntimeException e) {
            throw e;
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        });
  }

  public CompletableFuture<T> getByIdAsync(String id) {
    return http.getAsync(resourceUrl(id), null)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    descriptor.resourceClass());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, null));
  }

  public CompletableFuture<T> createAsync(T instance) {
    return http.postAsync(listUrl(), instance.toMap())
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    descriptor.resourceClass());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public CompletableFuture<T> updateAsync(T instance) {
    return http.putAsync(resourceUrl(requireResourceKey(instance)), instance.toMap())
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    descriptor.resourceClass());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public CompletableFuture<Boolean> deleteByIdAsync(String id) {
    return http.deleteAsync(resourceUrl(id))
        .thenApply(
            r -> {
              try (r) {
                return r.code() == HttpStatusCodes.OK.getCode()
                    || r.code() == HttpStatusCodes.NO_CONTENT.getCode();
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, false));
  }

  public CompletableFuture<QueryResult<T>> queryWithCountAsync(Map<String, String> params) {
    return http.getAsync(listUrl(), params)
        .thenApply(
            r -> {
              try (r) {
                List<Map<String, Object>> items =
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_LIST_TYPE);
                return new QueryResult<>(
                    deserializeList(items), Integer.parseInt(r.header("X-Total-Count", "0")));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, new QueryResult<>(Collections.emptyList(), 0)));
  }

  protected String listUrl() {
    return UrlPaths.join(descriptor.urlPath());
  }

  protected String resourceUrl(String key) {
    return UrlPaths.join(descriptor.urlPath(), key);
  }

  protected String propertyUrl(String key, String propertyName) {
    return UrlPaths.join(descriptor.urlPath(), key, propertyName);
  }

  protected String requireResourceKey(T instance) {
    String id = instance.getId();
    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException(
          descriptor.typeName() + " id must not be null or empty for update/delete");
    }
    return id;
  }

  protected List<T> deserializeList(List<Map<String, Object>> items) {
    List<T> result = new ArrayList<>();
    for (Map<String, Object> item : items) {
      result.add(Resource.fromMap(item, descriptor.resourceClass()));
    }
    return result;
  }

  protected static <R> R handleNotFoundAsync(Throwable e, R notFoundValue) {
    Throwable cause = unwrap(e);
    if (cause instanceof OperationFailureException ofe
        && ofe.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) {
      return notFoundValue;
    }
    if (cause instanceof RuntimeException re) {
      throw re;
    }
    if (cause instanceof Error err) {
      throw err;
    }
    throw new CompletionException(cause);
  }

  static Throwable unwrap(Throwable e) {
    if (e instanceof CompletionException && e.getCause() != null) {
      return e.getCause();
    }
    return e;
  }
}
