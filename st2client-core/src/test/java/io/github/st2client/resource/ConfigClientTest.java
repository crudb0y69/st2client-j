package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.Config;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/** Tests for {@link ConfigClient} using MockWebServer. */
class ConfigClientTest extends BaseMockServerTest {
  private ConfigClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new ConfigClient(http);
  }

  @Test
  void shouldUpdateByPackName() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody("{\"id\":\"cfg-1\",\"pack\":\"my-pack\",\"values\":{\"key\":\"val\"}}"));

    Config config = new Config();
    config.setPack("my-pack");
    config.setValues(Map.of("key", "val"));

    Config result = mgr.updateByPackName("my-pack", config);
    assertThat(result.getId()).isEqualTo("cfg-1");
    assertThat(result.getPack()).isEqualTo("my-pack");
    assertThat(result.getValues()).containsEntry("key", "val");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/configs/my-pack");
    assertThat(req.getMethod()).isEqualTo("PUT");
  }

  @Test
  void shouldUpdateByPackNameAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody("{\"id\":\"cfg-2\",\"pack\":\"async-pack\",\"values\":{\"a\":1}}"));

    Config config = new Config();
    config.setPack("async-pack");
    config.setValues(Map.of("a", 1));

    Config result = mgr.updateByPackNameAsync("async-pack", config).get();
    assertThat(result.getId()).isEqualTo("cfg-2");
    assertThat(result.getPack()).isEqualTo("async-pack");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/configs/async-pack");
    assertThat(req.getMethod()).isEqualTo("PUT");
  }

  @Test
  void shouldReturnNullOnUpdateByPackName404() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(404).setBody("{\"faultstring\":\"Config not found\"}"));

    Config config = new Config();
    config.setValues(Map.of());

    // 404 should throw OperationFailureException, not return null
    try {
      mgr.updateByPackName("missing-pack", config);
      // Should not reach here
      assertThat(false).isTrue();
    } catch (OperationFailureException e) {
      assertThat(e.getStatusCode()).isEqualTo(404);
    }
  }

  @Test
  void shouldThrowOnNullPackName() {
    Config config = new Config();
    try {
      mgr.updateByPackName(null, config);
      assertThat(false).isTrue();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).contains("packName must not be null");
    } catch (IOException e) {
      // Expected for sync methods
    }
  }

  @Test
  void shouldThrowOnNullInstance() {
    try {
      mgr.updateByPackName("my-pack", null);
      assertThat(false).isTrue();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).contains("instance must not be null");
    } catch (IOException e) {
      // Expected for sync methods
    }
  }
}
