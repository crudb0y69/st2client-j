package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;

/** Tests for {@link KeyValueClient} using MockWebServer. */
class KeyValueClientTest extends BaseMockServerTest {
  private KeyValueClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new KeyValueClient(http);
  }

  @Test
  void shouldGetByName() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"k1\",\"name\":\"mykey\",\"value\":\"myval\"}"));
    KeyValuePair kv = mgr.getByName("mykey");
    assertThat(kv.getValue()).isEqualTo("myval");
  }

  @Test
  void shouldReturnNullOn404() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(404));
    KeyValuePair kv = mgr.getByName("nope");
    assertThat(kv).isNull();
  }

  @Test
  void shouldSet() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"k2\",\"name\":\"newkey\",\"value\":\"newval\",\"secret\":true}"));
    KeyValuePair kv = mgr.set("newkey", "newval", true, "system");
    assertThat(kv.getName()).isEqualTo("newkey");
    assertThat(kv.getValue()).isEqualTo("newval");
  }

  @Test
  void shouldSetAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"k3\",\"name\":\"akey\",\"value\":\"aval\"}"));
    KeyValuePair kv = mgr.setAsync("akey", "aval", false, null).get();
    assertThat(kv.getName()).isEqualTo("akey");
  }

  @Test
  void shouldUpdateByName() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"k1\",\"name\":\"mykey\",\"value\":\"updated\"}"));
    KeyValuePair kv = new KeyValuePair();
    kv.setName("mykey");
    kv.setValue("updated");
    KeyValuePair result = mgr.update(kv);
    assertThat(result.getValue()).isEqualTo("updated");
  }

  @Test
  void shouldDeleteByName() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(204));
    assertThat(mgr.deleteById("mykey")).isTrue();
  }

  @Test
  void shouldDeleteByNameReturnFalseOn404() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(404));
    assertThat(mgr.deleteById("nope")).isFalse();
  }
}
