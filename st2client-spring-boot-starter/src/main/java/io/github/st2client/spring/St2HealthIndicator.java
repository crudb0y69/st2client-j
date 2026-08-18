package io.github.st2client.spring;

import io.github.st2client.St2Client;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Actuator {@link HealthIndicator} that reports StackStorm API connectivity. Registered
 * automatically when Actuator is on the classpath.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class St2HealthIndicator implements HealthIndicator {

  private static final long DEFAULT_CACHE_MILLIS = 10_000L;

  private record CacheEntry(Health health, long timestamp) {}

  private final St2Client client;
  private final long cacheMillis;
  private final AtomicReference<CacheEntry> cache = new AtomicReference<>();

  /**
   * Creates a health indicator with the default cache TTL of 10 seconds.
   *
   * @param client the StackStorm client to probe for health
   * @since 0.1.0
   */
  public St2HealthIndicator(St2Client client) {
    this(client, DEFAULT_CACHE_MILLIS);
  }

  St2HealthIndicator(St2Client client, long cacheMillis) {
    this.client = client;
    this.cacheMillis = cacheMillis;
  }

  /**
   * Performs a health check against the StackStorm API.
   *
   * <p>Results are cached for the configured TTL to avoid redundant API calls on repeated Actuator
   * polls. The probe invokes {@link St2Client#getUserInfo()} and reports {@code UP} on success or
   * {@code DOWN} with the error detail on failure.
   *
   * @return health status with endpoint and error details
   * @since 0.1.0
   */
  @Override
  public Health health() {
    long now = System.currentTimeMillis();
    CacheEntry entry = cache.get();
    if (entry != null && now - entry.timestamp() < cacheMillis) {
      return entry.health();
    }

    Health result = probe();
    cache.set(new CacheEntry(result, now));
    return result;
  }

  private Health probe() {
    try {
      client.getUserInfo();
      return Health.up().withDetail("endpoint", client.getConfig().getApiUrl()).build();
    } catch (Exception e) {
      return Health.down()
          .withDetail("endpoint", client.getConfig().getApiUrl())
          .withDetail("error", e.getMessage())
          .build();
    }
  }
}
