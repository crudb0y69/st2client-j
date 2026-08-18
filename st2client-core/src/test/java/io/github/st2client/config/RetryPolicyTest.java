package io.github.st2client.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;

class RetryPolicyTest {

  @Test
  void shouldCalculateExponentialBackoff() {
    RetryPolicy policy = RetryPolicy.builder().build();

    // initialDelay=1000ms, attempt 1 -> 1000, attempt 2 -> 2000, attempt 3 -> 4000
    assertThat(policy.calculateDelay(1)).isEqualTo(1000);
    assertThat(policy.calculateDelay(2)).isEqualTo(2000);
    assertThat(policy.calculateDelay(3)).isEqualTo(4000);
    assertThat(policy.calculateDelay(4)).isEqualTo(8000);
    assertThat(policy.calculateDelay(5)).isEqualTo(16000);
  }

  @Test
  void shouldCapAtMaxDelay() {
    RetryPolicy policy = RetryPolicy.builder().initialDelayMs(5000).maxDelayMs(20000).build();

    // attempt 1: 5000, attempt 2: 10000, attempt 3: 20000 (capped), attempt 4: 20000 (capped)
    assertThat(policy.calculateDelay(3)).isEqualTo(20000);
    assertThat(policy.calculateDelay(10)).isEqualTo(20000);
  }

  @Test
  void shouldHaveDefaultRetryOnStatus() {
    RetryPolicy policy = RetryPolicy.builder().build();
    assertThat(policy.getRetryOnStatus()).containsExactlyInAnyOrder(429, 500, 502, 503, 504);
  }

  @Test
  void shouldUseCustomRetryOnStatus() {
    RetryPolicy policy = RetryPolicy.builder().retryOnStatus(Set.of(429, 503)).build();
    assertThat(policy.getRetryOnStatus()).containsExactlyInAnyOrder(429, 503);
  }

  @Test
  void shouldBuildWithAllCustomValues() {
    RetryPolicy policy =
        RetryPolicy.builder()
            .maxRetries(5)
            .initialDelayMs(2000)
            .maxDelayMs(60000)
            .retryOnStatus(Set.of(500))
            .build();

    assertThat(policy.getMaxRetries()).isEqualTo(5);
    assertThat(policy.getInitialDelayMs()).isEqualTo(2000);
    assertThat(policy.getMaxDelayMs()).isEqualTo(60000);
    assertThat(policy.getRetryOnStatus()).containsExactly(500);
  }

  @Test
  void shouldRejectNegativeMaxRetries() {
    assertThatThrownBy(() -> RetryPolicy.builder().maxRetries(-1).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxRetries");
  }

  @Test
  void shouldRejectNegativeInitialDelay() {
    assertThatThrownBy(() -> RetryPolicy.builder().initialDelayMs(-1).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("initialDelayMs");
  }

  @Test
  void shouldRejectNegativeMaxDelay() {
    assertThatThrownBy(() -> RetryPolicy.builder().maxDelayMs(-1).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxDelayMs");
  }

  @Test
  void shouldRejectNullRetryOnStatus() {
    assertThatThrownBy(() -> RetryPolicy.builder().retryOnStatus(null).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("retryOnStatus");
  }

  @Test
  void shouldRejectEmptyRetryOnStatus() {
    assertThatThrownBy(() -> RetryPolicy.builder().retryOnStatus(Set.of()).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("retryOnStatus");
  }
}
