package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.TriggerInstance;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

class TriggerInstanceClientTest extends BaseMockServerTest {
  private TriggerInstanceClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new TriggerInstanceClient(http);
  }

  @Test
  void shouldReemit() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"ti1\",\"trigger\":\"core.event\"}"));
    TriggerInstance result = mgr.reemit("ti1");
    assertThat(result.getId()).isEqualTo("ti1");
    assertThat(result.getTrigger()).isEqualTo("core.event");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/triggerinstances/ti1/re_emit");
    assertThat(req.getMethod()).isEqualTo("POST");
  }

  @Test
  void shouldReemitAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"ti2\",\"trigger\":\"core.async\"}"));
    TriggerInstance result = mgr.reemitAsync("ti2").get();
    assertThat(result.getId()).isEqualTo("ti2");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/triggerinstances/ti2/re_emit");
    assertThat(req.getMethod()).isEqualTo("POST");
  }
}
