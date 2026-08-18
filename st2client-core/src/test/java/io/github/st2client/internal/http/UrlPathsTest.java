package io.github.st2client.internal.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UrlPathsTest {

  @Test
  void shouldJoinSegments() {
    assertThat(UrlPaths.join("actions", "core.local")).isEqualTo("/actions/core.local");
  }

  @Test
  void shouldEncodeSpecialCharacters() {
    assertThat(UrlPaths.join("keys", "name with space")).isEqualTo("/keys/name%20with%20space");
  }
}
