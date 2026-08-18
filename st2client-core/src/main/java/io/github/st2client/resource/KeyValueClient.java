package io.github.st2client.resource;

import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.http.HttpStatusCodes;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.KeyValuePair;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Key-Value Pairs. StackStorm keys are addressed by {@code name}
 * rather than {@code id}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class KeyValueClient extends ResourceClient<KeyValuePair> {

  /**
   * Constructs a new KeyValueClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public KeyValueClient(St2HttpClient http) {
    super(ResourceDescriptor.KEY_VALUE_PAIR, http);
  }

  /**
   * Retrieves a key-value pair by its ID.
   *
   * @param id the identifier of the key-value pair
   * @return the key-value pair, or null if not found
   * @throws IOException if the request fails
   * @since 0.1.0
   */
  @Override
  public KeyValuePair getById(String id) throws IOException {
    return getByName(id);
  }

  /**
   * Asynchronously retrieves a key-value pair by its ID.
   *
   * @param id the identifier of the key-value pair
   * @return a CompletableFuture completing with the key-value pair, or {@code null} if not found
   * @since 0.1.0
   */
  @Override
  public CompletableFuture<KeyValuePair> getByIdAsync(String id) {
    return getByNameAsync(id);
  }

  /**
   * Retrieves a key-value pair by its reference or ID.
   *
   * @param refOrId the reference or identifier
   * @return the key-value pair, or null if not found
   * @throws IOException if the request fails
   * @since 0.1.0
   */
  @Override
  public KeyValuePair getByRefOrId(String refOrId) throws IOException {
    return getByName(refOrId);
  }

  /**
   * Asynchronously retrieves a key-value pair by its reference or ID.
   *
   * @param refOrId the reference or identifier
   * @return a CompletableFuture completing with the key-value pair, or {@code null} if not found
   * @since 0.1.0
   */
  @Override
  public CompletableFuture<KeyValuePair> getByRefOrIdAsync(String refOrId) {
    return getByNameAsync(refOrId);
  }

  /**
   * Retrieves a key-value pair by its name.
   *
   * @param name the name of the key-value pair
   * @return the key-value pair, or null if not found
   * @throws IOException if the request fails
   * @since 0.1.0
   */
  @Override
  public KeyValuePair getByName(String name) throws IOException {
    try (Response r = http.get(resourceUrl(name), null)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          KeyValuePair.class);
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) return null;
      throw e;
    }
  }

  /**
   * Asynchronously retrieves a key-value pair by its name.
   *
   * @param name the name of the key-value pair
   * @return a CompletableFuture completing with the key-value pair, or {@code null} if not found
   * @since 0.1.0
   */
  @Override
  public CompletableFuture<KeyValuePair> getByNameAsync(String name) {
    return http.getAsync(resourceUrl(name), null)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    KeyValuePair.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, null));
  }

  /**
   * Retrieves a specific property from a key-value pair.
   *
   * @param name the name of the key-value pair
   * @param propertyName the property name to retrieve
   * @return the property value
   * @throws IOException if the request fails
   * @since 0.1.0
   */
  @Override
  public KeyValuePair getProperty(String name, String propertyName) throws IOException {
    try (Response r = http.get(propertyUrl(name, propertyName), null)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          KeyValuePair.class);
    } catch (OperationFailureException e) {
      if (e.getStatusCode() == HttpStatusCodes.NOT_FOUND.getCode()) return null;
      throw e;
    }
  }

  /**
   * Asynchronously retrieves a specific property from a key-value pair.
   *
   * @param name the name of the key-value pair
   * @param propertyName the property name to retrieve
   * @return a CompletableFuture completing with the property value, or {@code null} if not found
   * @since 0.1.0
   */
  @Override
  public CompletableFuture<KeyValuePair> getPropertyAsync(String name, String propertyName) {
    return http.getAsync(propertyUrl(name, propertyName), null)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    KeyValuePair.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .exceptionally(e -> handleNotFoundAsync(e, null));
  }

  /**
   * Deletes a key-value pair by its name.
   *
   * @param name the name of the key-value pair to delete
   * @return true if deleted successfully
   * @throws IOException if the request fails
   * @since 0.1.0
   */
  @Override
  public boolean deleteById(String name) throws IOException {
    return super.deleteById(name);
  }

  /**
   * Requires and returns the resource key for the given key-value pair instance.
   *
   * @param instance the key-value pair instance
   * @return the resource name
   * @throws IllegalArgumentException if the name is null or empty
   * @since 0.1.0
   */
  @Override
  protected String requireResourceKey(KeyValuePair instance) {
    String name = instance.getName();
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException(
          "KeyValuePair name must not be null or empty for update/delete");
    }
    return name;
  }

  /**
   * Creates or updates a key-value pair
   *
   * @param name the name
   * @param value the value
   * @param encrypt the encrypt
   * @param scope the scope
   * @return the key-value pair
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public KeyValuePair set(String name, String value, boolean encrypt, String scope)
      throws IOException {
    KeyValuePair kv = new KeyValuePair();
    kv.setName(name);
    kv.setValue(value);
    kv.setSecret(encrypt);
    kv.setScope(scope != null ? scope : "system");
    return create(kv);
  }

  /**
   * Asynchronously creates or updates a key-value pair
   *
   * @param name the name
   * @param value the value
   * @param encrypt the encrypt
   * @param scope the scope
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<KeyValuePair> setAsync(
      String name, String value, boolean encrypt, String scope) {
    KeyValuePair kv = new KeyValuePair();
    kv.setName(name);
    kv.setValue(value);
    kv.setSecret(encrypt);
    kv.setScope(scope != null ? scope : "system");
    return createAsync(kv);
  }
}
