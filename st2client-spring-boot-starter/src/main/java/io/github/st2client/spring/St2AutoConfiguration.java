package io.github.st2client.spring;

import io.github.st2client.St2Client;

import java.util.concurrent.Executor;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Auto-configuration for {@link St2Client}.
 *
 * <p>Activated when {@code st2.base-url} is configured ({@code @ConditionalOnProperty(name =
 * "st2.base-url")}) and {@link St2Client} is on the classpath
 * ({@code @ConditionalOnClass(St2Client.class)}).
 *
 * @author crudb0y69
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnClass(St2Client.class)
@EnableConfigurationProperties(St2Properties.class)
@ConditionalOnProperty(name = "st2.base-url")
public class St2AutoConfiguration {

  /**
   * Creates the primary {@link St2Client} bean from configuration properties.
   *
   * @param props the StackStorm configuration properties
   * @return a fully configured St2Client instance
   * @since 0.1.0
   */
  @Bean(destroyMethod = "close")
  @ConditionalOnMissingBean(St2Client.class)
  public St2Client st2Client(St2Properties props) {
    St2Properties.Auth auth = props.getAuth();
    St2Properties.Ssl ssl = props.getSsl();

    return St2Client.builder()
        .baseUrl(props.getBaseUrl())
        .authUrl(props.getAuthUrl())
        .apiUrl(props.getApiUrl())
        .verifySsl(ssl == null || ssl.isVerifySsl())
        .streamUrl(props.getStreamUrl())
        .apiVersion(props.getApiVersion())
        .token(auth != null ? auth.getToken() : null)
        .apiKey(auth != null ? auth.getApiKey() : null)
        .username(auth != null ? auth.getUsername() : null)
        .password(auth != null ? auth.getPassword() : null)
        .cacert(ssl != null ? ssl.getCacert() : null)
        .debug(props.isDebug())
        .connectTimeout(props.getConnectTimeout())
        .readTimeout(props.getReadTimeout())
        .writeTimeout(props.getWriteTimeout())
        .build();
  }

  /**
   * Creates the Actuator health indicator for StackStorm.
   *
   * <p>Conditional on {@link HealthIndicator} being on the classpath
   * ({@code @ConditionalOnClass(HealthIndicator.class)}) and the {@code st2.health.enabled}
   * property (defaults to {@code true}).
   *
   * @param client the St2Client to probe
   * @param props the StackStorm configuration properties
   * @return a health indicator cached per the configured TTL
   * @since 0.1.0
   */
  @Bean
  @ConditionalOnClass(HealthIndicator.class)
  @ConditionalOnProperty(name = "st2.health.enabled", havingValue = "true", matchIfMissing = true)
  @ConditionalOnMissingBean(St2HealthIndicator.class)
  public St2HealthIndicator st2HealthIndicator(St2Client client, St2Properties props) {
    St2Properties.Health health = props.getHealth();
    int cacheSeconds = health != null ? health.getCacheSeconds() : 10;
    return new St2HealthIndicator(client, cacheSeconds * 1000L);
  }

  /**
   * Creates the async task executor used by the StackStorm client.
   *
   * <p>Conditional on no existing {@link Executor} bean and the {@code st2.async.enabled} property
   * (defaults to {@code true}).
   *
   * @param props the StackStorm configuration properties
   * @return a configured {@link ThreadPoolTaskExecutor}
   * @since 0.1.0
   */
  @Bean(name = "st2TaskExecutor")
  @ConditionalOnMissingBean(name = "st2TaskExecutor")
  @ConditionalOnProperty(name = "st2.async.enabled", havingValue = "true", matchIfMissing = true)
  public ThreadPoolTaskExecutor st2TaskExecutor(St2Properties props) {
    St2Properties.Async async = props.getAsync();
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(async.getCorePoolSize());
    executor.setMaxPoolSize(async.getMaxPoolSize());
    executor.setQueueCapacity(async.getQueueCapacity());
    executor.setThreadNamePrefix(async.getThreadNamePrefix());
    executor.initialize();
    return executor;
  }
}
