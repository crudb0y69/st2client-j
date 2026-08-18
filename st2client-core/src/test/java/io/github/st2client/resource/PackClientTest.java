package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;

/** Tests for {@link PackClient} using MockWebServer. */
class PackClientTest extends BaseMockServerTest {
  private PackClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new PackClient(http);
  }

  @Test
  void shouldSearch() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("[{\"id\":\"p1\",\"name\":\"nginx\"}]"));
    List<Pack> results = mgr.search("nginx");
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("nginx");
  }

  @Test
  void shouldRegister() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"id\":\"p1\",\"name\":\"registered\"}"));
    Pack p = mgr.register(List.of("my-pack"), null);
    assertThat(p.getName()).isEqualTo("registered");
  }

  @Test
  void shouldInstall() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"execution_id\":\"exec-1\"}"));
    Map<String, Object> result = mgr.install(List.of("nginx"), false, true);
    assertThat(result).containsKey("execution_id");
  }

  @Test
  void shouldRemove() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"execution_id\":\"exec-2\"}"));
    Map<String, Object> result = mgr.remove(List.of("nginx"));
    assertThat(result).containsKey("execution_id");
  }

  @Test
  void shouldSearchAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":\"p1\",\"name\":\"async-pack\"}]"));
    List<Pack> results = mgr.searchAsync("async-pack").get();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("async-pack");
  }

  @Test
  void shouldInstallAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"execution_id\":\"exec-a1\"}"));
    Map<String, Object> result = mgr.installAsync(List.of("nginx"), false, true).get();
    assertThat(result).containsKey("execution_id");
  }

  @Test
  void shouldRemoveAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"execution_id\":\"exec-a2\"}"));
    Map<String, Object> result = mgr.removeAsync(List.of("nginx")).get();
    assertThat(result).containsKey("execution_id");
  }

  @Test
  void shouldRegisterAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"id\":\"rp1\",\"name\":\"reg-async\"}"));
    Pack p = mgr.registerAsync(List.of("p"), null).get();
    assertThat(p.getName()).isEqualTo("reg-async");
  }
}
