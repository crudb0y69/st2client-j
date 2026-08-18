package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.st2client.BaseMockServerTest;
import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;

/** Tests for generic {@link ResourceClient} CRUD operations using MockWebServer. */
class ResourceClientTest extends BaseMockServerTest {

  private ResourceClient<Action> manager;

  @BeforeEach
  void setUp() throws IOException {
    String baseUrl = server.url("/v1").toString();
    St2HttpClient http = new St2HttpClient(baseUrl, new OkHttpClient());
    manager = new ResourceClient<>(ResourceDescriptor.ACTION, http);
  }

  @Test
  void shouldGetAllAcrossPages() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Total-Count", "2")
            .setBody("[{\"id\":\"1\",\"name\":\"page-one\"}]"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Total-Count", "2")
            .setBody("[{\"id\":\"2\",\"name\":\"page-two\"}]"));

    List<Action> actions = manager.getAll(Map.of("limit", "1"));
    assertThat(actions).extracting(Action::getName).containsExactly("page-one", "page-two");
  }

  @Test
  void shouldIterateWithQueryWithCountSignature() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Total-Count", "2")
            .setBody("[{\"id\":\"1\",\"name\":\"a\"}]"));
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Total-Count", "2")
            .setBody("[{\"id\":\"2\",\"name\":\"b\"}]"));

    PagedIterator<Action> it = manager.iterate(null, 1);
    List<Action> actions = new java.util.ArrayList<>();
    it.forEachRemaining(actions::add);
    assertThat(actions).extracting(Action::getName).containsExactly("a", "b");
  }

  @Test
  void shouldGetAll() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("[{\"id\":\"1\",\"name\":\"test-action\",\"pack\":\"test\"}]"));
    List<Action> actions = manager.getAll();
    assertThat(actions).hasSize(1);
    assertThat(actions.get(0).getName()).isEqualTo("test-action");
  }

  @Test
  void shouldGetById() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("{\"id\":\"abc\",\"name\":\"found\"}"));
    Action a = manager.getById("abc");
    assertThat(a).isNotNull();
    assertThat(a.getName()).isEqualTo("found");
  }

  @Test
  void shouldReturnNullOn404() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(404));
    Action a = manager.getById("missing");
    assertThat(a).isNull();
  }

  @Test
  void shouldQuery() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("[{\"id\":\"1\",\"name\":\"hit\"}]"));
    List<Action> results = manager.query(Map.of("name", "hit"));
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("hit");
  }

  @Test
  void shouldReturnEmptyListOn404Query() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(404));
    List<Action> results = manager.query(Map.of("name", "nope"));
    assertThat(results).isEmpty();
  }

  @Test
  void shouldTreatEmptyListBodyAsEmptyList() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(200).setBody(""));
    assertThat(manager.query(Map.of("pack", "core"))).isEmpty();
  }

  @Test
  void shouldTreatObjectLiteralAsEmptyList() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
    assertThat(manager.query(Map.of("pack", "core"))).isEmpty();
  }

  @Test
  void shouldQueryWithCount() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Total-Count", "42")
            .setBody("[]"));
    QueryResult<Action> qr = manager.queryWithCount(Map.of("status", "active"));
    assertThat(qr.totalCount()).isEqualTo(42);
    assertThat(qr.items()).isEmpty();
  }

  @Test
  void shouldCreate() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("{\"id\":\"new-id\",\"name\":\"created\"}"));
    Action input = new Action();
    input.setName("created");
    Action result = manager.create(input);
    assertThat(result.getId()).isEqualTo("new-id");
    assertThat(result.getName()).isEqualTo("created");
  }

  @Test
  void shouldUpdate() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("{\"id\":\"abc\",\"name\":\"updated\"}"));
    Action input = new Action();
    input.setId("abc");
    input.setName("updated");
    Action result = manager.update(input);
    assertThat(result.getName()).isEqualTo("updated");
  }

  @Test
  void shouldDeleteById() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(204));
    boolean deleted = manager.deleteById("xyz");
    assertThat(deleted).isTrue();
  }

  @Test
  void shouldDeleteByIdReturnFalseOn404() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(404));
    boolean deleted = manager.deleteById("xyz");
    assertThat(deleted).isFalse();
  }

  @Test
  void shouldGetByName() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("[{\"id\":\"1\",\"name\":\"my-action\"}]"));
    Action a = manager.getByName("my-action");
    assertThat(a).isNotNull();
    assertThat(a.getName()).isEqualTo("my-action");
  }

  @Test
  void shouldGetByRefOrId() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("{\"id\":\"ref-123\",\"name\":\"by-ref\"}"));
    Action a = manager.getByRefOrId("ref-123");
    assertThat(a).isNotNull();
  }

  @Test
  void shouldGetProperty() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("{\"id\":\"a\",\"name\":\"prop\"}"));
    Action a = manager.getProperty("a", "parameters");
    assertThat(a).isNotNull();
  }

  @Test
  void shouldHandleErrorResponse() {
    server.enqueue(
        new MockResponse()
            .setResponseCode(500)
            .addHeader("Content-Type", "application/json")
            .setBody("{\"faultstring\":\"Internal error\"}"));
    assertThatThrownBy(() -> manager.getAll())
        .isInstanceOf(OperationFailureException.class)
        .hasMessageContaining("Internal error");
  }

  @Test
  void shouldHandleErrorWithoutFaultstring() {
    server.enqueue(new MockResponse().setResponseCode(403).setBody("Forbidden"));
    assertThatThrownBy(() -> manager.getAll())
        .isInstanceOf(OperationFailureException.class)
        .hasMessageContaining("Forbidden");
  }

  @Test
  void shouldGetAllAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("[{\"id\":\"1\",\"name\":\"async-action\",\"pack\":\"test\"}]"));
    List<Action> actions = manager.getAllAsync(null).get();
    assertThat(actions).hasSize(1);
    assertThat(actions.get(0).getName()).isEqualTo("async-action");
  }

  @Test
  void shouldGetByIdAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"async-1\",\"name\":\"found-async\"}"));
    Action a = manager.getByIdAsync("async-1").get();
    assertThat(a).isNotNull();
    assertThat(a.getName()).isEqualTo("found-async");
  }

  @Test
  void shouldCreateAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"new-async\",\"name\":\"created-async\"}"));
    Action input = new Action();
    input.setName("created-async");
    Action result = manager.createAsync(input).get();
    assertThat(result.getId()).isEqualTo("new-async");
  }

  @Test
  void shouldUpdateAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":\"a\",\"name\":\"updated-async\"}"));
    Action input = new Action();
    input.setId("a");
    input.setName("updated-async");
    Action result = manager.updateAsync(input).get();
    assertThat(result.getName()).isEqualTo("updated-async");
  }

  @Test
  void shouldDeleteByIdAsync() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(204));
    boolean deleted = manager.deleteByIdAsync("x").get();
    assertThat(deleted).isTrue();
  }

  @Test
  void shouldQueryWithCountAsync() throws Exception {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("X-Total-Count", "7")
            .setBody("[{\"id\":\"1\",\"name\":\"q\"}]"));
    QueryResult<Action> qr = manager.queryWithCountAsync(Map.of()).get();
    assertThat(qr.totalCount()).isEqualTo(7);
    assertThat(qr.items()).hasSize(1);
  }

  @Test
  void shouldThrowOnCreateWithNull() {
    assertThatThrownBy(() -> manager.create(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowOnUpdateWithNull() {
    assertThatThrownBy(() -> manager.update(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowOnCreateError() {
    server.enqueue(new MockResponse().setResponseCode(500));
    assertThatThrownBy(() -> manager.create(new Action()))
        .isInstanceOf(OperationFailureException.class);
  }

  @Test
  void shouldHandleAsyncError() {
    server.enqueue(new MockResponse().setResponseCode(500));
    assertThatThrownBy(() -> manager.getAllAsync(null).get())
        .hasCauseInstanceOf(OperationFailureException.class);
  }

  @Test
  void shouldReturnNullOnGetByNameNotFound() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(404));
    Action a = manager.getByName("nope");
    assertThat(a).isNull();
  }

  @Test
  void shouldThrowWhenMultipleGetByNameMatches() throws IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("[{\"id\":\"1\",\"name\":\"dup\"},{\"id\":\"2\",\"name\":\"dup\"}]"));
    assertThatThrownBy(() -> manager.getByName("dup"))
        .isInstanceOf(OperationFailureException.class)
        .hasMessageContaining("More than one")
        .hasMessageContaining("action");
  }

  @Test
  void shouldExposeOperationFailureOnAsyncNon404() {
    server.enqueue(
        new MockResponse()
            .setResponseCode(400)
            .addHeader("Content-Type", "application/json")
            .setBody("{\"faultstring\":\"bad request\"}"));
    assertThatThrownBy(() -> manager.getByIdAsync("x").get())
        .hasCauseInstanceOf(OperationFailureException.class);
  }

  @Test
  void shouldReturnNullOnGetByIdAsync404() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(404));
    Action a = manager.getByIdAsync("missing").get();
    assertThat(a).isNull();
  }

  @Test
  void shouldReturnFalseOnDeleteByIdAsync404() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(404));
    boolean deleted = manager.deleteByIdAsync("missing").get();
    assertThat(deleted).isFalse();
  }
}
