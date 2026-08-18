package io.github.st2client.resource;

import io.github.st2client.model.Resource;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * Iterator that automatically pages through resources using offset-based pagination.
 *
 * @param <T> the resource type
 * @author crudb0y69
 * @since 0.1.0
 */
public class PagedIterator<T extends Resource> implements Iterator<T> {

  private final Function<Map<String, String>, QueryResult<T>> fetcher;
  private final int pageSize;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private List<T> currentBatch;
  private int index;
  private int offset;
  private boolean done;

  /**
   * Constructs a new PagedIterator.
   *
   * @param fetcher the fetcher
   * @param pageSize the pageSize
   * @since 0.1.0
   */
  public PagedIterator(Function<Map<String, String>, QueryResult<T>> fetcher, int pageSize) {
    this.fetcher = fetcher;
    this.pageSize = pageSize;
    this.offset = 0;
    this.index = 0;
    this.done = false;
    this.currentBatch = List.of();
  }

  /**
   * Returns whether another resource is available, fetching the next page if needed.
   *
   * @return {@code true} if a subsequent {@link #next()} will return an element
   * @since 0.1.0
   */
  @Override
  public boolean hasNext() {
    lock.readLock().lock();
    try {
      if (index < currentBatch.size()) return true;
      if (done) return false;
    } finally {
      lock.readLock().unlock();
    }
    lock.writeLock().lock();
    try {

      if (index < currentBatch.size()) return true;
      if (done) return false;
      fetchNext();
      return !currentBatch.isEmpty();
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Returns the next element in the iteration
   *
   * @return the resource
   * @since 0.1.0
   */
  @Override
  public T next() {
    if (!hasNext()) throw new NoSuchElementException();
    lock.writeLock().lock();
    try {
      return currentBatch.get(index++);
    } finally {
      lock.writeLock().unlock();
    }
  }

  private void fetchNext() {
    Map<String, String> params =
        Map.of("limit", String.valueOf(pageSize), "offset", String.valueOf(offset));
    QueryResult<T> result = fetcher.apply(params);
    currentBatch = result.items() != null ? result.items() : List.of();
    index = 0;
    offset += currentBatch.size();
    if (currentBatch.isEmpty()
        || currentBatch.size() < pageSize
        || (result.totalCount() > 0 && offset >= result.totalCount())) {
      done = true;
    }
  }
}
