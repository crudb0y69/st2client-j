package io.github.st2client.spring;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.St2Client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Tests for {@link St2AutoConfiguration}.
 *
 * @since 0.1.0
 */
class St2AutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(St2AutoConfiguration.class));

  @Test
  void shouldNotCreateBeanWhenBaseUrlMissing() {
    contextRunner.run(ctx -> assertThat(ctx).doesNotHaveBean(St2Client.class));
  }

  @Test
  void shouldCreateSt2ClientWhenBaseUrlSet() {
    contextRunner
        .withPropertyValues("st2.base-url=http://localhost")
        .run(ctx -> assertThat(ctx).hasSingleBean(St2Client.class));
  }

  @Test
  void shouldBindProperties() {
    contextRunner
        .withPropertyValues(
            "st2.base-url=http://stackstorm.example.com",
            "st2.auth-url=http://stackstorm.example.com:9100",
            "st2.api-url=http://stackstorm.example.com:9101/v1",
            "st2.auth.username=admin",
            "st2.auth.password=secret",
            "st2.auth.token=explicit-token",
            "st2.auth.api-key=explicit-key",
            "st2.debug=true")
        .run(
            ctx -> {
              St2Client client = ctx.getBean(St2Client.class);
              assertThat(client.getConfig().getBaseUrl())
                  .isEqualTo("http://stackstorm.example.com");
              assertThat(client.getConfig().getAuthUrl())
                  .isEqualTo("http://stackstorm.example.com:9100");
              assertThat(client.getConfig().getApiUrl())
                  .isEqualTo("http://stackstorm.example.com:9101/v1");
              assertThat(client.getConfig().getToken()).isEqualTo("explicit-token");
              assertThat(client.getConfig().getApiKey()).isEqualTo("explicit-key");
              assertThat(client.getConfig().isDebug()).isTrue();
            });
  }

  @Test
  void shouldBindCustomApiUrlDifferentFromDerivedDefault() {
    contextRunner
        .withPropertyValues(
            "st2.base-url=https://st2.example.com", "st2.api-url=https://st2.example.com/api/v1")
        .run(
            ctx -> {
              St2Client client = ctx.getBean(St2Client.class);
              assertThat(client.getConfig().getApiUrl())
                  .isEqualTo("https://st2.example.com/api/v1");
            });
  }

  @Test
  void shouldCreateHealthIndicator() {
    contextRunner
        .withPropertyValues("st2.base-url=http://localhost")
        .run(ctx -> assertThat(ctx).hasSingleBean(St2HealthIndicator.class));
  }

  @Test
  void shouldBindSslCacert(@TempDir Path tempDir) throws IOException {
    byte[] certBytes =
        Base64.getDecoder()
            .decode(
                "MIIDGDCCAgCgAwIBAgIQNsD5VeljT7VNocZoNpYXiDANBgkqhkiG9w0BAQsFADAUMRIwEAYDVQQDDAlsb2NhbGhvc3QwHhcNMjYwNjI1MTI1MDQ0WhcNMjcwNjI1MTMwMDQzWjAUMRIwEAYDVQQDDAlsb2NhbGhvc3QwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDGE4of0JF5L+9SmHhQipCX1Lhre/6cKcxzczH+BwkT58OQJcEbam5ew9qvjIlFlGKgAJeRL+iAxHpnWjiN7urWUsCUS2fsewbrlRRNjp6A1Ca2Wec++6i3vllheImqf00d1jJHUn0LI+Z1kAyHP5GqCefD7ud6rJuBrPskF5pJ59Sozrlr9jZYrQ4u9o4oBDCscKjqf7zIlPLsjuIj22gL21w3e35TPsketdS+V0ObbBI6G5O4N+n6p59aWLdIbhjcB4UOlBkWA9agqhsnGK2JzXY3T5pgpMRn7L3vAziAw2AZGiHr9oItRi3dg3a/DVRGwLvX3VuD3OUqV0Dbg1TZAgMBAAGjZjBkMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwEwFAYDVR0RBA0wC4IJbG9jYWxob3N0MB0GA1UdDgQWBBTQz6g5Vhak2GCSw2P7AJOw9oEYrTANBgkqhkiG9w0BAQsFAAOCAQEAbxEe/TrMclNQc6tO0VBzWVWjr64Qt3EAUf4/Cl9MTjN+hqzgzqJpDrX5fm6JWeNBfAJnjafywk2qYNKqe4zew40aUlPBYPzgimD598RaJFZvbgPJ1KheP31pBBnJR718uNcVjFVKF57NOm/7Qsdz4Tf7P3FiDaudqW13iQ0HbsFo3W8olpsvrtvbeg4I9oB2wNXTpQ15S5dv9pNe4Kvg/aVa1NfltEn9JbvWMh9ekUHC/R4FBy0eG4OHIkoZAepsPfsOBOmmUq/2G48G78xOJAXXuQ7VCXEtN3oMk5Sd++slRwcALGPqKjNRyS1dLCk3Fr0lwBsldK2lTitUg9fNrw==");
    Path certFile = tempDir.resolve("ca.pem");
    Files.write(certFile, certBytes);

    contextRunner
        .withPropertyValues(
            "st2.base-url=http://example.com",
            "st2.ssl.cacert=" + certFile.toString().replace('\\', '/'))
        .run(
            ctx -> {
              St2Client client = ctx.getBean(St2Client.class);
              assertThat(client.getConfig().getCacert())
                  .isEqualTo(certFile.toString().replace('\\', '/'));
            });
  }

  @Test
  @org.junit.jupiter.api.Disabled("Requires ST2_ALLOW_INSECURE_SSL=true environment variable")
  void shouldBindSslVerifySsl() {
    contextRunner
        .withPropertyValues("st2.base-url=http://example.com", "st2.ssl.verify-ssl=false")
        .run(
            ctx -> {
              St2Client client = ctx.getBean(St2Client.class);
              assertThat(client.getConfig().isVerifySsl()).isFalse();
            });
  }

  @Test
  void shouldDisableHealthIndicator() {
    contextRunner
        .withPropertyValues("st2.base-url=http://localhost", "st2.health.enabled=false")
        .run(ctx -> assertThat(ctx).doesNotHaveBean(St2HealthIndicator.class));
  }

  @Test
  void shouldBindHealthCacheSeconds() {
    contextRunner
        .withPropertyValues("st2.base-url=http://localhost", "st2.health.cache-seconds=30")
        .run(
            ctx -> {
              St2Properties props = ctx.getBean(St2Properties.class);
              assertThat(props.getHealth().getCacheSeconds()).isEqualTo(30);
            });
  }

  @Test
  void shouldBindStreamUrl() {
    contextRunner
        .withPropertyValues("st2.base-url=http://example.com", "st2.stream-url=http://stream:9102")
        .run(
            ctx -> {
              St2Client client = ctx.getBean(St2Client.class);
              assertThat(client.getConfig().getStreamUrl()).isEqualTo("http://stream:9102");
            });
  }

  @Test
  void shouldBindApiVersion() {
    contextRunner
        .withPropertyValues("st2.base-url=http://example.com", "st2.api-version=v2")
        .run(
            ctx -> {
              St2Client client = ctx.getBean(St2Client.class);
              assertThat(client.getConfig().getApiVersion()).isEqualTo("v2");
            });
  }

  @Test
  void shouldCreateNamedExecutorWhenAnotherExecutorExists() {
    contextRunner
        .withUserConfiguration(ExistingExecutorConfig.class)
        .withPropertyValues("st2.base-url=http://localhost")
        .run(
            ctx -> {
              assertThat(ctx.containsBean("st2TaskExecutor")).isTrue();
              assertThat(ctx.getBeansOfType(Executor.class)).hasSizeGreaterThanOrEqualTo(2);
            });
  }

  @Test
  void shouldCreateAsyncExecutorByDefault() {
    contextRunner
        .withPropertyValues("st2.base-url=http://localhost")
        .run(
            ctx -> {
              assertThat(ctx).hasSingleBean(Executor.class);
              ThreadPoolTaskExecutor executor = ctx.getBean(ThreadPoolTaskExecutor.class);
              assertThat(executor.getCorePoolSize()).isEqualTo(4);
              assertThat(executor.getMaxPoolSize()).isEqualTo(8);
              assertThat(executor.getQueueCapacity()).isEqualTo(100);
              assertThat(executor.getThreadNamePrefix()).isEqualTo("st2-async-");
            });
  }

  @Test
  void shouldNotCreateExecutorWhenAsyncDisabled() {
    contextRunner
        .withPropertyValues("st2.base-url=http://localhost", "st2.async.enabled=false")
        .run(ctx -> assertThat(ctx).doesNotHaveBean(Executor.class));
  }

  @Test
  void shouldBindAsyncPoolProperties() {
    contextRunner
        .withPropertyValues(
            "st2.base-url=http://localhost",
            "st2.async.core-pool-size=8",
            "st2.async.max-pool-size=16",
            "st2.async.queue-capacity=200",
            "st2.async.thread-name-prefix=st2-pool-")
        .run(
            ctx -> {
              ThreadPoolTaskExecutor executor = ctx.getBean(ThreadPoolTaskExecutor.class);
              assertThat(executor.getCorePoolSize()).isEqualTo(8);
              assertThat(executor.getMaxPoolSize()).isEqualTo(16);
              assertThat(executor.getQueueCapacity()).isEqualTo(200);
              assertThat(executor.getThreadNamePrefix()).isEqualTo("st2-pool-");
            });
  }

  static class ExistingExecutorConfig {
    @org.springframework.context.annotation.Bean
    Executor applicationExecutor() {
      return Runnable::run;
    }
  }
}
