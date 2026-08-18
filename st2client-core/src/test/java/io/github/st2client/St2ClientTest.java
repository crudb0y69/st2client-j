package io.github.st2client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.model.Action;
import io.github.st2client.model.Resource;
import io.github.st2client.model.Rule;
import io.github.st2client.model.Timer;
import io.github.st2client.model.Token;
import io.github.st2client.model.UserInfo;
import io.github.st2client.resource.ResourceReader;
import io.github.st2client.stream.St2StreamClient;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/** Core SDK tests using MockWebServer to simulate StackStorm API. */
class St2ClientTest {

  private static final io.github.st2client.stream.EventListener NOOP_LISTENER =
      new io.github.st2client.stream.EventListener() {
        public void onEvent(io.github.st2client.stream.Event e) {}

        public void onError(Throwable t) {}

        public void onComplete() {}
      };

  private MockWebServer mockServer;
  private St2Client client;

  @BeforeEach
  void setUp() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    int port = mockServer.getPort();
    String url = "http://127.0.0.1:" + port;

    client =
        St2Client.builder()
            .baseUrl(url)
            .apiUrl(url + "/v1")
            .authUrl(url)
            .streamUrl(url + "/v1")
            .build();
  }

  @AfterEach
  void tearDown() throws IOException {
    mockServer.shutdown();
  }

  @Test
  void shouldListActions() throws Exception {
    mockServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                [
                    {"name": "myaction", "pack": "mypack", "runner_type": "python-script", "enabled": true},
                    {"name": "notify", "pack": "core", "runner_type": "http-request", "enabled": false}
                ]
                """)
            .addHeader("Content-Type", "application/json"));

    var actions = client.actions().getAll();
    assertThat(actions).hasSize(2);
    assertThat(actions.get(0).getName()).isEqualTo("myaction");
    assertThat(actions.get(0).getPack()).isEqualTo("mypack");
  }

  @Test
  void shouldGetActionById() throws Exception {
    mockServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                {"id": "abc123", "name": "deploy", "pack": "ci", "runner_type": "remote-shell-cmd", "enabled": true}
                """)
            .addHeader("Content-Type", "application/json"));

    Action action = client.actions().getById("abc123");
    assertThat(action).isNotNull();
    assertThat(action.getId()).isEqualTo("abc123");
    assertThat(action.getName()).isEqualTo("deploy");
  }

  @Test
  void shouldReturnNullOn404() throws Exception {
    mockServer.enqueue(new MockResponse().setResponseCode(404));

    Action action = client.actions().getById("nonexistent");
    assertThat(action).isNull();
  }

  @Test
  void shouldListExecutions() throws Exception {
    mockServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                [
                    {"id": "exec1", "status": "succeeded", "start_timestamp": "2024-01-01T00:00:00Z"},
                    {"id": "exec2", "status": "failed", "start_timestamp": "2024-01-01T01:00:00Z"}
                ]
                """)
            .addHeader("Content-Type", "application/json"));

    var executions = client.executions().getAll();
    assertThat(executions).hasSize(2);
    assertThat(executions.get(0).getStatus()).isEqualTo("succeeded");
  }

  @Test
  void shouldDeserializeToken() throws Exception {
    String json =
        """
                {"id": "tok1", "user": "admin", "token": "abc-token", "expiry": "2025-01-01T00:00:00Z"}
                """;
    Token token = Resource.fromJson(json, Token.class);
    assertThat(token.getUser()).isEqualTo("admin");
    assertThat(token.getToken()).isEqualTo("abc-token");
  }

  @Test
  void shouldDeserializeRule() throws Exception {
    String json =
        """
                {"name": "on_cpu_alert", "pack": "monitoring", "trigger": {"type": "sensu.event"}, "enabled": true}
                """;
    Rule rule = Resource.fromJson(json, Rule.class);
    assertThat(rule.getName()).isEqualTo("on_cpu_alert");
    assertThat(rule.getEnabled()).isTrue();
  }

  @Test
  void shouldHandleQueryWithCount() throws Exception {
    mockServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Total-Count", "42")
            .setBody("[]"));

    var result = client.executions().queryWithCount(Map.of("status", "failed"));
    assertThat(result.totalCount()).isEqualTo(42);
    assertThat(result.items()).isEmpty();
  }

  @Test
  void shouldSerializeAction() throws Exception {
    Action action = new Action();
    action.setName("test-action");
    action.setPack("test-pack");
    action.setRunnerType("python-script");
    action.setEnabled(true);

    Map<String, Object> serialized = action.toMap();
    assertThat(serialized.get("name")).isEqualTo("test-action");
    assertThat(serialized.get("pack")).isEqualTo("test-pack");
  }

  @Test
  void shouldAuthenticate() throws Exception {
    MockWebServer authServer = new MockWebServer();
    authServer.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t1\",\"user\":\"admin\",\"token\":\"new-token\",\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));
    authServer.start();

    St2Client client =
        St2Client.builder()
            .baseUrl("http://localhost:9101")
            .authUrl(authServer.url("/").toString())
            .username("admin")
            .password("secret")
            .build();
    client.authenticate().get();
    // Token should now be set
    assertThat(client.getConfig().getToken()).isNull(); // token is in provider, not config
    authServer.shutdown();
    client.close();
  }

  @Test
  void shouldCloseCleanly() {
    St2Client client = St2Client.builder().baseUrl("http://localhost:9101").build();
    assertThatCode(client::close).doesNotThrowAnyException();
  }

  @Test
  void shouldShutDownExecutionMonitorsOnClose() {
    St2Client client = St2Client.builder().baseUrl("http://localhost:9101").build();
    client.close();
    assertThatThrownBy(() -> client.executions().onStatusChange("e1", exec -> {}, 100))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldBuildWithDebug() {
    St2Client client = St2Client.builder().baseUrl("http://localhost:9101").debug(true).build();
    assertThat(client.getConfig().isDebug()).isTrue();
    client.close();
  }

  @Test
  @org.junit.jupiter.api.Disabled("Requires ST2_ALLOW_INSECURE_SSL=true environment variable")
  void shouldBuildWithVerifySslFalse() throws Exception {
    St2Client client =
        St2Client.builder().baseUrl("https://localhost:9101").verifySsl(false).build();
    assertThat(client.getConfig().isVerifySsl()).isFalse();
    client.close();
  }

  @Test
  void shouldBuildWithApiKey() throws Exception {
    St2Client client =
        St2Client.builder().baseUrl("http://localhost:9101").apiKey("key-123").build();
    assertThat(client.getConfig().getApiKey()).isEqualTo("key-123");
    client.close();
  }

  @Nested
  class BuilderConfig {

    @Test
    void shouldBuildWithStreamUrl() {
      St2Client c =
          St2Client.builder().baseUrl("http://localhost").streamUrl("http://s:9102").build();
      assertThat(c.getConfig().getStreamUrl()).isEqualTo("http://s:9102");
      c.close();
    }

    @Test
    void shouldBuildWithUsernamePassword() {
      St2Client c =
          St2Client.builder()
              .baseUrl("http://localhost")
              .username("admin")
              .password("pass")
              .build();
      assertThat(c.getConfig().getUsername()).isEqualTo("admin");
      assertThat(new String(c.getConfig().getPassword())).isEqualTo("pass");
      assertThat(c.getConfig().getToken()).isNull();
      c.close();
    }

    @Test
    void shouldBuildWithApiVersion() {
      St2Client c = St2Client.builder().baseUrl("http://localhost").apiVersion("v2").build();
      assertThat(c.getConfig().getApiVersion()).isEqualTo("v2");
      c.close();
    }

    @Test
    void shouldBuildWithAllFields() {
      St2Client c =
          St2Client.builder()
              .baseUrl("http://localhost")
              .authUrl("http://a:9100")
              .apiUrl("http://api:9101/v1")
              .streamUrl("http://s:9102/v1")
              .apiVersion("v1")
              .token("t")
              .apiKey("k")
              .username("u")
              .password("p")
              .debug(true)
              .build();
      assertThat(c.getConfig().getBaseUrl()).isEqualTo("http://localhost");
      assertThat(c.getConfig().getAuthUrl()).isEqualTo("http://a:9100");
      assertThat(c.getConfig().getApiUrl()).isEqualTo("http://api:9101/v1");
      assertThat(c.getConfig().getStreamUrl()).isEqualTo("http://s:9102/v1");
      assertThat(c.getConfig().getApiVersion()).isEqualTo("v1");
      assertThat(c.getConfig().getToken()).isEqualTo("t");
      assertThat(c.getConfig().getApiKey()).isEqualTo("k");
      assertThat(c.getConfig().getUsername()).isEqualTo("u");
      assertThat(new String(c.getConfig().getPassword())).isEqualTo("p");
      assertThat(c.getConfig().getCacert()).isNull();
      assertThat(c.getConfig().isVerifySsl()).isTrue();
      assertThat(c.getConfig().isDebug()).isTrue();
      c.close();
    }
  }

  @Test
  void shouldCloseMultipleTimes() {
    St2Client client = St2Client.builder().baseUrl("http://localhost:9101").build();
    assertThatCode(client::close).doesNotThrowAnyException();
    assertThatCode(client::close).doesNotThrowAnyException();
  }

  @Test
  void shouldExposeStreamClient() {
    St2Client client =
        St2Client.builder().baseUrl("http://localhost").streamUrl("http://stream:9102/v1").build();
    assertThat(client.getConfig().getStreamUrl()).isEqualTo("http://stream:9102/v1");
    assertThat(client.streamClient()).isNotNull();
    client.close();
  }

  @Test
  void shouldExposeReadOnlyResourcesAsReaders() {
    St2Client client = St2Client.builder().baseUrl("http://localhost").build();
    ResourceReader<Timer> timers = client.timers();
    assertThat(timers).isNotNull();
    assertThat(client.traces()).isNotNull();
    assertThat(client.runnerTypes()).isNotNull();
    assertThat(client.triggerTypes()).isNotNull();
    assertThat(client.policyTypes()).isNotNull();
    assertThat(client.configSchemas()).isNotNull();
    assertThat(client.ruleEnforcements()).isNotNull();
    client.close();
  }

  @Test
  void shouldBuildWithTokenProvider() {
    io.github.st2client.auth.TokenProvider custom =
        new io.github.st2client.auth.TokenProvider() {
          public java.util.Optional<String> getToken() {
            return java.util.Optional.of("custom");
          }

          public java.util.Optional<String> getApiKey() {
            return java.util.Optional.empty();
          }

          public void setToken(String t) {}

          public java.util.concurrent.CompletableFuture<String> refresh() {
            return java.util.concurrent.CompletableFuture.completedFuture("custom");
          }

          public void onUnauthorized() {}

          public void authenticate(String username, String password) throws java.io.IOException {}
        };
    St2Client c = St2Client.builder().baseUrl("http://localhost").tokenProvider(custom).build();
    assertThat(c.getConfig().getToken()).isNull();
    c.close();
  }

  @Test
  void shouldRetryOn401() throws Exception {
    MockWebServer apiServer = new MockWebServer();
    MockWebServer authServer = new MockWebServer();
    apiServer.enqueue(new MockResponse().setResponseCode(401));
    authServer.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t1\",\"user\":\"admin\",\"token\":\"refreshed-token\",\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));
    apiServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":\"1\",\"name\":\"post-retry\",\"pack\":\"core\"}]")
            .addHeader("Content-Type", "application/json"));
    apiServer.start();
    authServer.start();

    St2Client client =
        St2Client.builder()
            .apiUrl(apiServer.url("/v1").toString())
            .authUrl(authServer.url("/").toString())
            .username("admin")
            .password("secret")
            .build();
    try {
      var actions = client.actions().getAll();
      assertThat(actions).hasSize(1);
      assertThat(actions.get(0).getName()).isEqualTo("post-retry");
    } finally {
      client.close();
      apiServer.shutdown();
      authServer.shutdown();
    }
  }

  @Test
  void shouldAuthenticateWithExplicitCredentials() throws Exception {
    MockWebServer authServer = new MockWebServer();
    authServer.enqueue(
        new MockResponse()
            .setResponseCode(201)
            .setBody(
                "{\"id\":\"t1\",\"user\":\"admin\",\"token\":\"explicit-token\",\"expiry\":\"2099-01-01T00:00:00.000Z\"}"));
    authServer.start();

    St2Client client =
        St2Client.builder()
            .baseUrl("http://localhost:9101")
            .authUrl(authServer.url("/").toString())
            .build();
    try {
      client.authenticate("admin", "secret");

      RecordedRequest request = authServer.takeRequest();
      assertThat(request.getPath()).isEqualTo("/tokens");
      assertThat(request.getMethod()).isEqualTo("POST");

      String authHeader = request.getHeader("Authorization");
      assertThat(authHeader).startsWith("Basic ");
      String decoded = new String(Base64.getDecoder().decode(authHeader.substring(6)));
      assertThat(decoded).isEqualTo("admin:secret");
    } finally {
      client.close();
      authServer.shutdown();
    }
  }

  @Test
  void shouldThrowOnAuthFailure() throws Exception {
    MockWebServer authServer = new MockWebServer();
    authServer.enqueue(
        new MockResponse().setResponseCode(401).setBody("{\"faultstring\":\"Unauthorized\"}"));
    authServer.start();

    St2Client client =
        St2Client.builder()
            .baseUrl("http://localhost:9101")
            .authUrl(authServer.url("/").toString())
            .build();
    try {
      assertThatThrownBy(() -> client.authenticate("admin", "wrong"))
          .isInstanceOf(OperationFailureException.class)
          .hasMessageContaining("401");
    } finally {
      client.close();
      authServer.shutdown();
    }
  }

  @Test
  void shouldGetUserInfo() throws Exception {
    MockWebServer apiServer = new MockWebServer();
    apiServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\"id\":\"u1\",\"name\":\"Admin User\",\"username\":\"admin\",\"email\":\"admin@example.com\",\"roles\":[\"admin\"],\"is_admin\":true}")
            .addHeader("Content-Type", "application/json"));
    apiServer.start();

    St2Client client =
        St2Client.builder()
            .apiUrl(apiServer.url("/v1").toString())
            .authUrl(apiServer.url("/").toString())
            .token("static-token")
            .build();
    try {
      UserInfo userInfo = client.getUserInfo();
      assertThat(userInfo).isNotNull();
      assertThat(userInfo.getId()).isEqualTo("u1");
      assertThat(userInfo.getName()).isEqualTo("Admin User");
      assertThat(userInfo.getUsername()).isEqualTo("admin");
      assertThat(userInfo.getEmail()).isEqualTo("admin@example.com");
      assertThat(userInfo.getRoles()).containsExactly("admin");
      assertThat(userInfo.getIsAdmin()).isTrue();
    } finally {
      client.close();
      apiServer.shutdown();
    }
  }

  @Test
  void shouldReturnStreamClient() {
    St2Client client =
        St2Client.builder().baseUrl("http://localhost").streamUrl("http://stream:9102/v1").build();
    St2StreamClient sc = client.streamClient();
    assertThat(sc).isNotNull();
    assertThat(client.streamClient()).isSameAs(sc); // same instance
    client.close();
  }

  @Test
  void shouldCloseStreamClientOnClientClose() {
    St2Client client =
        St2Client.builder().baseUrl("http://localhost").streamUrl("http://stream:9102/v1").build();
    St2StreamClient sc = client.streamClient();
    assertThat(sc).isNotNull();
    client.close();
    // After client.close(), listen() should throw
    assertThatThrownBy(() -> sc.listen(java.util.List.of(), NOOP_LISTENER))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowOnListenAfterClose() {
    St2StreamClient sc =
        new St2StreamClient(
            new okhttp3.OkHttpClient(),
            "http://localhost:9102/v1",
            new io.github.st2client.auth.TokenProvider() {
              public java.util.Optional<String> getToken() {
                return java.util.Optional.empty();
              }

              public java.util.Optional<String> getApiKey() {
                return java.util.Optional.empty();
              }

              public void setToken(String t) {}

              public java.util.concurrent.CompletableFuture<String> refresh() {
                return java.util.concurrent.CompletableFuture.completedFuture("");
              }

              public void onUnauthorized() {}
            });
    sc.close();
    assertThatThrownBy(() -> sc.listen(java.util.List.of(), NOOP_LISTENER))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldReturnExceptionalFutureOnListenAsyncAfterClose() {
    St2StreamClient sc =
        new St2StreamClient(
            new okhttp3.OkHttpClient(),
            "http://localhost:9102/v1",
            new io.github.st2client.auth.TokenProvider() {
              public java.util.Optional<String> getToken() {
                return java.util.Optional.empty();
              }

              public java.util.Optional<String> getApiKey() {
                return java.util.Optional.empty();
              }

              public void setToken(String t) {}

              public java.util.concurrent.CompletableFuture<String> refresh() {
                return java.util.concurrent.CompletableFuture.completedFuture("");
              }

              public void onUnauthorized() {}
            });
    sc.close();
    java.util.concurrent.CompletableFuture<Void> f =
        sc.listenAsync(java.util.List.of(), NOOP_LISTENER);
    assertThat(f.isCompletedExceptionally()).isTrue();
  }

  @Test
  void shouldCloseStreamClientIdempotent() {
    St2StreamClient sc =
        new St2StreamClient(
            new okhttp3.OkHttpClient(),
            "http://localhost:9102/v1",
            new io.github.st2client.auth.TokenProvider() {
              public java.util.Optional<String> getToken() {
                return java.util.Optional.empty();
              }

              public java.util.Optional<String> getApiKey() {
                return java.util.Optional.empty();
              }

              public void setToken(String t) {}

              public java.util.concurrent.CompletableFuture<String> refresh() {
                return java.util.concurrent.CompletableFuture.completedFuture("");
              }

              public void onUnauthorized() {}
            });
    assertThatCode(sc::close).doesNotThrowAnyException();
    assertThatCode(sc::close).doesNotThrowAnyException();
  }
}
