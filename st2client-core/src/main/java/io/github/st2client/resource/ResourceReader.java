package io.github.st2client.resource;

import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Read operations for a StackStorm resource collection.
 *
 * @param <T> the resource model type
 * @since 0.1.0
 */
public interface ResourceReader<T extends Resource> {

  List<T> getAll() throws IOException;

  List<T> getAll(Map<String, String> params) throws IOException;

  PagedIterator<T> iterate(Map<String, String> filters, int pageSize);

  T getById(String id) throws IOException;

  T getByName(String name) throws IOException;

  CompletableFuture<T> getByNameAsync(String name);

  T getByRefOrId(String refOrId) throws IOException;

  CompletableFuture<T> getByRefOrIdAsync(String refOrId);

  T getProperty(String id, String propertyName) throws IOException;

  CompletableFuture<T> getPropertyAsync(String id, String propertyName);

  List<T> query(Map<String, String> params) throws IOException;

  QueryResult<T> queryWithCount(Map<String, String> params) throws IOException;

  CompletableFuture<List<T>> getAllAsync(Map<String, String> params);

  CompletableFuture<T> getByIdAsync(String id);

  CompletableFuture<QueryResult<T>> queryWithCountAsync(Map<String, String> params);
}
