package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/** Tests for {@link ExecutionClient} using MockWebServer. */
class ExecutionClientTest extends BaseMockServerTest {
  private ExecutionClient mgr;

  @BeforeEach
  void setUp() throws IOException {
    St2HttpClient http = new St2HttpClient(server.url("/v1").toString(), new OkHttpClient());
    mgr = new ExecutionClient(http);
  }

  @AfterEach
  void tearDown() throws IOException {
    mgr.shutdownStatusMonitors();
  }

  @Test
  void shouldReRun() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"rerun-1\",\"status\":\"running\"}"));
    Execution e = mgr.reRun("exec-1", Map.of(), List.of(), List.of(), 0);
    assertThat(e.getId()).isEqualTo("rerun-1");
  }

  @Test
  void shouldGetOutput() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("stdout output here"));
    String out = mgr.getOutput("exec-1", "stdout");
    assertThat(out).contains("stdout output");
  }

  @Test
  void shouldGetResult() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"result\":\"ok\"}"));
    String result = mgr.getResult("exec-1");
    assertThat(result).contains("ok");
  }

  @Test
  void shouldPause() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"pausing\"}"));
    Execution e = mgr.pause("exec-1");
    assertThat(e.getStatus()).isEqualTo("pausing");
  }

  @Test
  void shouldResume() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"resuming\"}"));
    Execution e = mgr.resume("exec-1");
    assertThat(e.getStatus()).isEqualTo("resuming");
  }

  @Test
  void shouldGetChildren() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":\"child-1\"},{\"id\":\"child-2\"}]"));
    List<Execution> children = mgr.getChildren("exec-1", 1);
    assertThat(children).hasSize(2);
  }

  @Test
  void shouldReRunAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"id\":\"r1\",\"status\":\"running\"}"));
    Execution e = mgr.reRunAsync("e1", Map.of(), List.of(), List.of(), 0).get();
    assertThat(e.getId()).isEqualTo("r1");
  }

  @Test
  void shouldGetOutputAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("async output"));
    String out = mgr.getOutputAsync("e1", null).get();
    assertThat(out).contains("async output");
  }

  @Test
  void shouldPauseAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"id\":\"e1\",\"status\":\"pausing\"}"));
    Execution e = mgr.pauseAsync("e1").get();
    assertThat(e.getStatus()).isEqualTo("pausing");
  }

  @Test
  void shouldGetResultAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("async result"));
    String r = mgr.getResultAsync("e1").get();
    assertThat(r).contains("async result");
  }

  @Test
  void shouldResumeAsync() throws Exception {
    server.enqueue(
        new MockResponse().setResponseCode(200).setBody("{\"id\":\"e1\",\"status\":\"resuming\"}"));
    Execution e = mgr.resumeAsync("e1").get();
    assertThat(e.getStatus()).isEqualTo("resuming");
  }

  @Test
  void shouldGetChildrenAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":\"c1\"}]"));
    List<Execution> children = mgr.getChildrenAsync("e1", 0).get();
    assertThat(children).hasSize(1);
  }

  @Test
  void shouldInspect() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody("{\"valid\":true,\"errors\":[],\"warnings\":[]}"));
    Map<String, Object> result = mgr.inspect("workflow: test\n  chain: []");
    assertThat(result).containsEntry("valid", true);
    assertThat(result.get("errors")).isNotNull();

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/workflows/inspect");
    assertThat(req.getMethod()).isEqualTo("POST");
    assertThat(req.getHeader("Content-Type")).startsWith("text/plain");
    assertThat(req.getBody().readUtf8()).isEqualTo("workflow: test\n  chain: []");
  }

  @Test
  void shouldInspectAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody("{\"valid\":false,\"errors\":[\"bad syntax\"]}"));
    Map<String, Object> result = mgr.inspectAsync("broken yaml").get();
    assertThat(result).containsEntry("valid", false);

    RecordedRequest req = server.takeRequest();
    assertThat(req.getPath()).isEqualTo("/v1/workflows/inspect");
    assertThat(req.getHeader("Content-Type")).startsWith("text/plain");
    assertThat(req.getBody().readUtf8()).isEqualTo("broken yaml");
  }

  @Test
  void shouldOnStatusChangeDetectStatusChange() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"running\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"succeeded\"}"));

    CountDownLatch latch = new CountDownLatch(2);
    AtomicReference<String> lastStatus = new AtomicReference<>();

    mgr.onStatusChange(
        "exec-1",
        exec -> {
          lastStatus.set(exec.getStatus());
          latch.countDown();
        },
        100);

    latch.await(2, TimeUnit.SECONDS);
    assertThat(lastStatus.get()).isEqualTo("succeeded");
  }

  @Test
  void shouldCancelStatusMonitor() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"running\"}"));

    AtomicInteger callCount = new AtomicInteger(0);
    var future =
        mgr.onStatusChange(
            "exec-1",
            exec -> {
              callCount.incrementAndGet();
            },
            100);

    Thread.sleep(200);
    mgr.cancelStatusMonitor("exec-1", future);
    int countAfterCancel = callCount.get();

    Thread.sleep(300);
    assertThat(callCount.get()).isEqualTo(countAfterCancel);
  }

  @Test
  void shouldStopStatusMonitorOnTerminalStatus() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"succeeded\"}"));

    CountDownLatch latch = new CountDownLatch(1);
    mgr.onStatusChange(
        "exec-1",
        exec -> {
          latch.countDown();
        },
        50);

    assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
    Thread.sleep(200);
    assertThat(server.getRequestCount()).isEqualTo(1);
  }

  @Test
  void shouldRejectStatusMonitorAfterShutdown() {
    mgr.shutdownStatusMonitors();
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> mgr.onStatusChange("exec-1", exec -> {}, 100))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldOnStatusChangeIgnoreSameStatus() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"running\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"running\"}"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"exec-1\",\"status\":\"succeeded\"}"));

    AtomicInteger callCount = new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(1);

    mgr.onStatusChange(
        "exec-1",
        exec -> {
          callCount.incrementAndGet();
          latch.countDown();
        },
        100);

    latch.await(2, TimeUnit.SECONDS);
    assertThat(callCount.get()).isEqualTo(1);
  }
}
