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
import okhttp3.mockwebserver.RecordedRequest;

/** Tests for {@link ActionClient} using MockWebServer. */
class ActionClientTest extends BaseMockServerTest {
  private ActionClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new ActionClient(http);
  }

  @Test
  void shouldGetEntryPoint() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"entry_point\":\"main.py\"}"));
    String ep = mgr.getEntryPoint("test.ref");
    assertThat(ep).isEqualTo("main.py");
  }

  @Test
  void shouldClone() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"cloned\",\"name\":\"cloned-action\"}"));
    Action a = mgr.clone("src.ref", "dest-pack", "dest-action", false);
    assertThat(a.getId()).isEqualTo("cloned");
    assertThat(a.getName()).isEqualTo("cloned-action");
  }

  @Test
  void shouldGetEntryPointAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"entry_point\":\"async.py\"}"));
    String ep = mgr.getEntryPointAsync("t.ref").get();
    assertThat(ep).isEqualTo("async.py");
  }

  @Test
  void shouldCloneAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"c2\",\"name\":\"cloned-async\"}"));
    Action a = mgr.cloneAsync("s.ref", "dp", "da", true).get();
    assertThat(a.getId()).isEqualTo("c2");
  }

  @Test
  void shouldDeleteAction() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(204));
    boolean result = mgr.deleteAction("test.ref", true);
    assertThat(result).isTrue();

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/actions/test.ref?remove_files=true");
    assertThat(req.getMethod()).isEqualTo("DELETE");
  }

  @Test
  void shouldDeleteActionWithoutRemoveFiles() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200));
    boolean result = mgr.deleteAction("action.ref", false);
    assertThat(result).isTrue();

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/actions/action.ref?remove_files=false");
  }

  @Test
  void shouldReturnFalseOnDeleteAction404() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(404).setBody("{\"faultstring\":\"Action not found\"}"));
    boolean result = mgr.deleteAction("missing.ref", false);
    assertThat(result).isFalse();
  }

  @Test
  void shouldDeleteActionAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(204));
    boolean result = mgr.deleteActionAsync("async.ref", true).get();
    assertThat(result).isTrue();

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/actions/async.ref?remove_files=true");
  }

  @Test
  void shouldReturnFalseOnDeleteActionAsync404() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(404).setBody("{\"faultstring\":\"Not found\"}"));
    boolean result = mgr.deleteActionAsync("missing.ref", false).get();
    assertThat(result).isFalse();
  }
}
