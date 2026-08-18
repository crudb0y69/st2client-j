package io.github.st2client.stream;

/**
 * Callback interface for receiving SSE events from the StackStorm stream API. Implementations
 * handle incoming events, errors, and connection lifecycle.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public interface EventListener {

  /**
   * Invoked when a new event is received from the stream.
   *
   * @param event the received event
   */
  void onEvent(Event event);

  /**
   * Invoked when an error occurs during streaming.
   *
   * @param error the error that occurred
   */
  void onError(Throwable error);

  /**
   * Invoked when the connection is closed, either after {@link St2StreamClient#close()} or by a
   * server-initiated disconnect without reconnection.
   */
  void onComplete();
}
