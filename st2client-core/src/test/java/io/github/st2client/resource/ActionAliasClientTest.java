package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.ActionAlias;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

class ActionAliasClientTest extends BaseMockServerTest {
  private ActionAliasClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new ActionAliasClient(http);
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
    assertThat(result.getActionRef()).isEqualTo("core.local");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/actionalias/match");
    assertThat(req.getMethod()).isEqualTo("POST");
    assertThat(req.getBody().readUtf8()).contains("\"name\":\"test-alias\"");
  }

  @Test
  void shouldMatchAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"name\":\"async-match\",\"pack\":\"ap\",\"action_ref\":\"core.remote\"}"));
    ActionAlias input = new ActionAlias();
    input.setName("async-alias");
    input.setPack("ap");
    ActionAlias result = mgr.matchAsync(input).get();
    assertThat(result.getName()).isEqualTo("async-match");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/actionalias/match");
    assertThat(req.getMethod()).isEqualTo("POST");
  }
}
