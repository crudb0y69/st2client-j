package io.github.st2client.resource;

import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Create, update, and delete operations for a StackStorm resource collection.
 *
 * @param <T> the resource model type
 * @since 0.1.0
 */
public interface ResourceWriter<T extends Resource> {

  T create(T instance) throws IOException;

  T update(T instance) throws IOException;

  boolean deleteById(String id) throws IOException;

  boolean delete(T instance) throws IOException;

  CompletableFuture<Boolean> deleteAsync(T instance);

  CompletableFuture<T> createAsync(T instance);

  CompletableFuture<T> updateAsync(T instance);

  CompletableFuture<Boolean> deleteByIdAsync(String id);
}
