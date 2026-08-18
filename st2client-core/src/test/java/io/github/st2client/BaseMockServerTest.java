package io.github.st2client;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import okhttp3.mockwebserver.MockWebServer;

/**
 * Base test class for tests that use MockWebServer. Manages the server lifecycle (create, start,
 * shutdown) to eliminate boilerplate duplication across test classes.
 *
 * @since 0.1.0
 */
public abstract class BaseMockServerTest {

  protected MockWebServer server;

  @BeforeEach
  protected void initServer() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterEach
  protected void shutdownServer() throws IOException {
    server.shutdown();
  }
}
