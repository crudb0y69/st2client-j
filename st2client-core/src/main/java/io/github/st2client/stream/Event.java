package io.github.st2client.stream;

import io.github.st2client.model.Resource;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Represents a single SSE event received from the StackStorm stream API. Contains the event id,
 * type, timestamp, and parsed JSON payload.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public record Event(String id, String eventType, long timestamp, Map<String, Object> data) {

  private static final Logger log = LoggerFactory.getLogger(Event.class);
  private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

  /**
   * Returns an unmodifiable view of the event data payload.
   *
   * @return an unmodifiable map of the event data
   */
  @Override
  public Map<String, Object> data() {
    return Collections.unmodifiableMap(data);
  }

  /**
   * Parses an OkHttp SSE event into an {@code Event} instance.
   *
   * @param rawEvent the raw SSE event data string (the {@code data} field)
   * @param eventType the SSE event type (e.g. "execution", "action")
   * @return parsed Event, or {@code null} if rawEvent is null or empty
   */
  public static Event fromSseEvent(String rawEvent, String eventType) {
    if (rawEvent == null || rawEvent.isEmpty()) {
      return null;
    }
    try {
      Map<String, Object> data = Resource.readJson(rawEvent, MAP_TYPE);
      Object tsObj = data.get("timestamp");
      long timestamp = 0L;
      if (tsObj instanceof Number) {
        timestamp = ((Number) tsObj).longValue();
      } else if (tsObj instanceof String) {
        timestamp = parseTimestamp((String) tsObj);
      }
      String id = data.get("id") instanceof String ? (String) data.get("id") : null;
      return new Event(id, eventType, timestamp, data);
    } catch (Exception e) {
      log.debug("Failed to parse SSE event data, wrapping as raw: {}", e.getMessage());
      return new Event(null, eventType, System.currentTimeMillis(), Map.of("raw", rawEvent));
    }
  }

  /**
   * Parses an ISO-8601 timestamp string to epoch milliseconds.
   *
   * @param s the ISO-8601 timestamp string
   * @return the epoch milliseconds, or the current time if parsing fails
   */
  private static long parseTimestamp(String s) {
    try {
      return java.time.Instant.parse(s).toEpochMilli();
    } catch (Exception e) {
      return System.currentTimeMillis();
    }
  }

  @Override
  public String toString() {
    return "Event{type=" + eventType + ", id=" + id + ", timestamp=" + timestamp + "}";
  }
}
