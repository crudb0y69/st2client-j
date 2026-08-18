package io.github.st2client.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract base class for all StackStorm resource models. Subclasses must implement getId() and
 * define @JsonProperty-annotated fields.
 *
 * <p>Equality is identity-based on {@link #getId()} in subclasses: two instances with a null id are
 * not equal (except by reference), so they are unsafe as {@link java.util.Set} elements until
 * persisted.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public abstract class Resource {

  private static final ObjectMapper MAPPER =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
  public static final TypeReference<List<Map<String, Object>>> MAP_LIST_TYPE =
      new TypeReference<>() {};

  /**
   * Returns a copy of the shared ObjectMapper. Mutating the copy does not affect SDK
   * deserialization.
   *
   * @return a copy of the mapper
   * @since 0.1.0
   */
  public static ObjectMapper mapper() {
    return MAPPER.copy();
  }

  /**
   * Parses JSON, treating a blank or {@code {}} body as an empty list when {@code type} is a list.
   *
   * @param json the JSON string, possibly null or blank
   * @param type the target type
   * @return the deserialized value
   * @since 0.1.0
   */
  public static <T> T readJson(String json, TypeReference<T> type) {
    try {
      return MAPPER.readValue(normalize(json, type), type);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(new IOException(e));
    }
  }

  /**
   * Serializes a value to JSON.
   *
   * @param value the value to serialize
   * @return the JSON string
   * @since 0.1.0
   */
  public static String writeJson(Object value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(new IOException(e));
    }
  }

  private static String normalize(String json, TypeReference<?> type) {
    if (json == null || json.isBlank()) {
      return isList(type) ? "[]" : "{}";
    }
    if (isList(type) && "{}".equals(json.trim())) {
      return "[]";
    }
    return json;
  }

  private static boolean isList(TypeReference<?> type) {
    Type t = type.getType();
    return t instanceof ParameterizedType pt && pt.getRawType() == List.class;
  }

  /**
   * Returns the id.
   *
   * @return the id value
   * @since 0.1.0
   */
  @JsonIgnore
  public abstract String getId();

  /**
   * Serializes this resource instance to a Map for JSON submission.
   *
   * @return the map representation of this resource
   * @since 0.1.0
   */
  @JsonIgnore
  public Map<String, Object> toMap() {
    return MAPPER.convertValue(this, MAP_TYPE);
  }

  /**
   * Deserializes a resource from a Map.
   *
   * @param <T> the resource type
   * @param map the source map
   * @param clazz the target resource class
   * @return the deserialized resource instance
   * @since 0.1.0
   */
  public static <T extends Resource> T fromMap(Map<String, Object> map, Class<T> clazz) {
    return MAPPER.convertValue(map, clazz);
  }

  /**
   * Deserializes a resource from a JSON string.
   *
   * @param <T> the resource type
   * @param json the JSON string
   * @param clazz the target resource class
   * @return the deserialized resource instance
   * @throws RuntimeException if deserialization fails
   * @since 0.1.0
   */
  public static <T extends Resource> T fromJson(String json, Class<T> clazz) {
    try {
      return MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize " + clazz.getSimpleName(), e);
    }
  }

  /**
   * Returns a string representation of this resource.
   *
   * @return a string containing the class name and identifier
   * @since 0.1.0
   */
  @Override
  public String toString() {
    String id = getId();
    return getClass().getSimpleName() + (id != null ? " id=" + id : "");
  }
}
