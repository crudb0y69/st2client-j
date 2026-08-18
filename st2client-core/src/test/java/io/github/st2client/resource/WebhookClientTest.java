package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.ActionAlias;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/** Tests for {@link WebhookClient} using MockWebServer. */
class WebhookClientTest extends BaseMockServerTest {
  private WebhookClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new WebhookClient(http);
  }

  @Test
  void shouldPostGenericWebhook() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"status\":\"success\"}"));
    Map<String, Object> result =
        mgr.postGenericWebhook("my-pack.my-trigger", Map.of("k", "v"), "trace-123");
    assertThat(result).containsEntry("status", "success");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/webhooks/st2");
    String body = req.getBody().readUtf8();
    assertThat(body).contains("\"trigger\":\"my-pack.my-trigger\"");
    assertThat(body).contains("\"trace_tag\":\"trace-123\"");
  }

  @Test
  void shouldPostGenericWebhookWithoutTraceTag() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));
    Map<String, Object> result = mgr.postGenericWebhook("trigger.ref", Map.of("data", 1), null);
    assertThat(result).containsEntry("ok", true);

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/webhooks/st2");
    String body = req.getBody().readUtf8();
    assertThat(body).contains("\"trigger\":\"trigger.ref\"");
    assertThat(body).doesNotContain("trace_tag");
  }

  @Test
  void shouldMatch() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"name\":\"matched\",\"pack\":\"p\",\"action_ref\":\"core.local\"}"));
    ActionAlias input = new ActionAlias();
    input.setName("test-alias");
    input.setPack("p");
    ActionAlias result = mgr.match(input);
    assertThat(result.getName()).isEqualTo("matched");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/webhooks/st2/alias/match");
  }

  @Test
  void shouldPostGenericWebhookAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"status\":\"async-ok\"}"));
    Map<String, Object> result =
        mgr.postGenericWebhookAsync("async.trigger", Map.of(), "tag-1").get();
    assertThat(result).containsEntry("status", "async-ok");
  }

  @Test
  void shouldMatchAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"name\":\"async-match\",\"pack\":\"ap\"}"));
    ActionAlias input = new ActionAlias();
    input.setName("async-alias");
    input.setPack("ap");
    ActionAlias result = mgr.matchAsync(input).get();
    assertThat(result.getName()).isEqualTo("async-match");
  }

  @Test
  void shouldReturnEmptyMapOnWebhook404() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(404).setBody("{\"faultstring\":\"Trigger not found\"}"));
    try {
      mgr.postGenericWebhook("missing.trigger", Map.of(), null);
      assertThat(false).isTrue();
    } catch (OperationFailureException e) {
      assertThat(e.getStatusCode()).isEqualTo(404);
    }
  }

  @Test
  void shouldReturnNullOnMatch404() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(404).setBody("{\"faultstring\":\"Alias not found\"}"));
    ActionAlias input = new ActionAlias();
    input.setName("missing");
    try {
      mgr.match(input);
      assertThat(false).isTrue();
    } catch (OperationFailureException e) {
      assertThat(e.getStatusCode()).isEqualTo(404);
    }
  }

  @Test
  void shouldHandleNullPayloadGracefully() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));
    Map<String, Object> result = mgr.postGenericWebhook("trigger", null, null);
    assertThat(result).containsEntry("ok", true);

    RecordedRequest req = server.takeRequest();
    String body = req.getBody().readUtf8();
    assertThat(body).contains("\"payload\":{}");
  }
}
