package io.github.st2client.config;

import java.util.Set;

/**
 * Configures retry behavior for transient HTTP failures. Use {@link #builder()} to create
 * instances.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class RetryPolicy {

  private static final int DEFAULT_MAX_RETRIES = 3;
  private static final long DEFAULT_INITIAL_DELAY_MS = 1000;
  private static final long DEFAULT_MAX_DELAY_MS = 30000;
  private static final Set<Integer> DEFAULT_RETRY_ON_STATUS = Set.of(429, 500, 502, 503, 504);

  private final int maxRetries;
  private final long initialDelayMs;
  private final long maxDelayMs;
  private final Set<Integer> retryOnStatus;

  private RetryPolicy(Builder builder) {
    this.maxRetries = builder.maxRetries;
    this.initialDelayMs = builder.initialDelayMs;
    this.maxDelayMs = builder.maxDelayMs;
    this.retryOnStatus = Set.copyOf(builder.retryOnStatus);
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public long getInitialDelayMs() {
    return initialDelayMs;
  }

  public long getMaxDelayMs() {
    return maxDelayMs;
  }

  public Set<Integer> getRetryOnStatus() {
    return retryOnStatus;
  }

  /**
   * Calculate delay for the given attempt using exponential backoff. delay = initialDelayMs *
   * 2^(attempt-1), capped at maxDelayMs.
   *
   * @param attempt the attempt number (1-indexed)
   * @return the delay in milliseconds
   * @since 0.1.0
   */
  public long calculateDelay(int attempt) {
    long delay = initialDelayMs * (1L << Math.min(attempt - 1, 62));
    return Math.min(delay, maxDelayMs);
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Builder for creating {@link RetryPolicy} instances. */
  public static class Builder {
    private int maxRetries = DEFAULT_MAX_RETRIES;
    private long initialDelayMs = DEFAULT_INITIAL_DELAY_MS;
    private long maxDelayMs = DEFAULT_MAX_DELAY_MS;
    private Set<Integer> retryOnStatus = DEFAULT_RETRY_ON_STATUS;

    private Builder() {}

    /**
     * Sets the maximum number of retry attempts.
     *
     * @param maxRetries the max retries, must be >= 0
     * @return this builder
     * @since 0.1.0
     */
    public Builder maxRetries(int maxRetries) {
      this.maxRetries = maxRetries;
      return this;
    }

    /**
     * Sets the initial delay in milliseconds before the first retry.
     *
     * @param initialDelayMs the initial delay, must be >= 0
     * @return this builder
     * @since 0.1.0
     */
    public Builder initialDelayMs(long initialDelayMs) {
      this.initialDelayMs = initialDelayMs;
      return this;
    }

    /**
     * Sets the maximum delay in milliseconds between retries.
     *
     * @param maxDelayMs the max delay, must be >= 0
     * @return this builder
     * @since 0.1.0
     */
    public Builder maxDelayMs(long maxDelayMs) {
      this.maxDelayMs = maxDelayMs;
      return this;
    }

    /**
     * Sets the set of HTTP status codes that trigger a retry.
     *
     * @param retryOnStatus the status codes, must not be null or empty
     * @return this builder
     * @since 0.1.0
     */
    public Builder retryOnStatus(Set<Integer> retryOnStatus) {
      this.retryOnStatus = retryOnStatus;
      return this;
    }

    /**
     * Builds the RetryPolicy with validation of all parameters.
     *
     * @return a new RetryPolicy instance
     * @throws IllegalArgumentException if any parameter is invalid
     * @since 0.1.0
     */
    public RetryPolicy build() {
      if (maxRetries < 0)
        throw new IllegalArgumentException("maxRetries must be >= 0, got " + maxRetries);
      if (initialDelayMs < 0)
        throw new IllegalArgumentException("initialDelayMs must be >= 0, got " + initialDelayMs);
      if (maxDelayMs < 0)
        throw new IllegalArgumentException("maxDelayMs must be >= 0, got " + maxDelayMs);
      if (retryOnStatus == null || retryOnStatus.isEmpty()) {
        throw new IllegalArgumentException("retryOnStatus must not be null or empty");
      }
      return new RetryPolicy(this);
    }
  }
}
