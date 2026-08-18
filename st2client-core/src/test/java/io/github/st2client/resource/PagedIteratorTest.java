package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.st2client.model.Resource;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests for {@link PagedIterator}. */
class PagedIteratorTest {

  private static class TestResource extends Resource {
    private String id;
    private String name;

    TestResource(String id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

  @Test
  void shouldIterateSinglePage() {
    List<TestResource> items =
        List.of(new TestResource("1", "a"), new TestResource("2", "b"), new TestResource("3", "c"));

    PagedIterator<TestResource> it = new PagedIterator<>(params -> new QueryResult<>(items, 3), 10);

    List<TestResource> result = new ArrayList<>();
    it.forEachRemaining(result::add);
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getName()).isEqualTo("a");
  }

  @Test
  void shouldIterateMultiplePages() {
    List<TestResource> page1 = List.of(new TestResource("1", "p1a"), new TestResource("2", "p1b"));
    List<TestResource> page2 = List.of(new TestResource("3", "p2a"));

    PagedIterator<TestResource> it =
        new PagedIterator<>(
            params -> {
              int offset = Integer.parseInt(params.get("offset"));
              if (offset == 0) return new QueryResult<>(page1, 3);
              return new QueryResult<>(page2, 3);
            },
            2);

    List<TestResource> result = new ArrayList<>();
    it.forEachRemaining(result::add);
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getName()).isEqualTo("p1a");
    assertThat(result.get(2).getName()).isEqualTo("p2a");
  }

  @Test
  void shouldReturnFalseHasNextOnEmptyResult() {
    PagedIterator<TestResource> it =
        new PagedIterator<>(params -> new QueryResult<>(List.of(), 0), 10);

    assertThat(it.hasNext()).isFalse();
  }

  @Test
  void shouldThrowNoSuchElementOnEmpty() {
    PagedIterator<TestResource> it =
        new PagedIterator<>(params -> new QueryResult<>(List.of(), 0), 10);

    assertThatThrownBy(it::next).isInstanceOf(java.util.NoSuchElementException.class);
  }

  @Test
  void shouldThrowNoSuchElementAfterExhaustion() {
    List<TestResource> items = List.of(new TestResource("1", "only"));
    PagedIterator<TestResource> it = new PagedIterator<>(params -> new QueryResult<>(items, 1), 10);

    assertThat(it.hasNext()).isTrue();
    it.next();
    assertThat(it.hasNext()).isFalse();
    assertThatThrownBy(it::next).isInstanceOf(java.util.NoSuchElementException.class);
  }

  @Test
  void shouldHandleNullItemsGracefully() {
    PagedIterator<TestResource> it = new PagedIterator<>(params -> new QueryResult<>(null, 0), 10);

    assertThat(it.hasNext()).isFalse();
  }

  @Test
  void shouldPassCorrectOffsetAndLimitParams() {
    List<String> capturedParams = new ArrayList<>();

    List<TestResource> page1 = List.of(new TestResource("1", "a"), new TestResource("2", "b"));
    List<TestResource> page2 = List.of(new TestResource("3", "c"));

    PagedIterator<TestResource> it =
        new PagedIterator<>(
            params -> {
              capturedParams.add(params.get("offset") + ":" + params.get("limit"));
              int offset = Integer.parseInt(params.get("offset"));
              if (offset == 0) return new QueryResult<>(page1, 3);
              return new QueryResult<>(page2, 3);
            },
            2);

    while (it.hasNext()) it.next();

    assertThat(capturedParams).containsExactly("0:2", "2:2");
  }
}
