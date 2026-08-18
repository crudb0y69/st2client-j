package io.github.st2client.stream;

/**
 * Strategy for determining the delay before reconnecting after a stream disconnect. Implementations
 * define the backoff behavior for reconnection attempts.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public interface ReconnectPolicy {

  /**
   * Calculates the delay in milliseconds before the next reconnect attempt.
   *
   * @param attemptNumber the current attempt number (1-indexed)
   * @return delay in milliseconds before reconnecting
   */
  long delayMs(int attemptNumber);

  /**
   * Default exponential backoff policy: starts at 1 second, doubles each attempt, caps at 30
   * seconds. This instance is shared and immutable; safe for concurrent use.
   */
  ReconnectPolicy DEFAULT = new ExponentialBackoffReconnectPolicy();

  /**
   * Exponential backoff reconnect policy with configurable initial delay, maximum delay, and
   * multiplier parameters.
   */
  class ExponentialBackoffReconnectPolicy implements ReconnectPolicy {

    private final long initialDelayMs;
    private final long maxDelayMs;
    private final double multiplier;

    /**
     * Creates an ExponentialBackoffReconnectPolicy with default settings: 1 second initial delay,
     * 30 second maximum, 2.0 multiplier.
     */
    public ExponentialBackoffReconnectPolicy() {
      this(1000L, 30000L, 2.0);
    }

    /**
     * Creates an ExponentialBackoffReconnectPolicy with custom parameters.
     *
     * @param initialDelayMs the initial delay in milliseconds
     * @param maxDelayMs the maximum delay in milliseconds
     * @param multiplier the exponential multiplier
     */
    public ExponentialBackoffReconnectPolicy(
        long initialDelayMs, long maxDelayMs, double multiplier) {
      this.initialDelayMs = initialDelayMs;
      this.maxDelayMs = maxDelayMs;
      this.multiplier = multiplier;
    }

    /**
     * Calculates the delay for the given attempt using exponential backoff.
     *
     * @param attemptNumber the current attempt number (1-indexed)
     * @return the calculated delay in milliseconds, capped at the maximum
     */
    @Override
    public long delayMs(int attemptNumber) {
      double delay = initialDelayMs * Math.pow(multiplier, attemptNumber - 1);
      return Math.min((long) delay, maxDelayMs);
    }

    /**
     * Returns the initial delay in milliseconds.
     *
     * @return the initial delay
     */
    public long getInitialDelayMs() {
      return initialDelayMs;
    }

    /**
     * Returns the maximum delay in milliseconds.
     *
     * @return the maximum delay
     */
    public long getMaxDelayMs() {
      return maxDelayMs;
    }

    /**
     * Returns the exponential multiplier.
     *
     * @return the multiplier
     */
    public double getMultiplier() {
      return multiplier;
    }
  }
}
