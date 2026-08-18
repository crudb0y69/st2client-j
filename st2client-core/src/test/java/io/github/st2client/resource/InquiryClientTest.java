package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.Inquiry;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

class InquiryClientTest extends BaseMockServerTest {
  private InquiryClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new InquiryClient(http);
  }

  @Test
  void shouldRespond() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"inq1\",\"status\":\"completed\"}"));
    Inquiry result = mgr.respond("inq1", Map.of("response", "approve"));
    assertThat(result.getId()).isEqualTo("inq1");
    assertThat(result.getStatus()).isEqualTo("completed");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/inquiries/inq1/respond");
    assertThat(req.getMethod()).isEqualTo("PUT");
    assertThat(req.getBody().readUtf8()).contains("\"response\":\"approve\"");
  }

  @Test
  void shouldRespondAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"id\":\"inq2\",\"status\":\"done\"}"));
    Inquiry result = mgr.respondAsync("inq2", Map.of("response", "ok")).get();
    assertThat(result.getId()).isEqualTo("inq2");

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/inquiries/inq2/respond");
    assertThat(req.getMethod()).isEqualTo("PUT");
  }
}
