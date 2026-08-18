package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm configuration for a pack, containing key-value settings that customize
 * pack behavior.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Config extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("pack")
  private String pack;

  @JsonProperty("values")
  private Map<String, Object> values;

  /**
   * Returns the id.
   *
   * @return the id value
   * @since 0.1.0
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the id to set
   * @since 0.1.0
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Returns the pack.
   *
   * @return the pack value
   * @since 0.1.0
   */
  public String getPack() {
    return pack;
  }

  /**
   * Sets the pack.
   *
   * @param pack the pack to set
   * @since 0.1.0
   */
  public void setPack(String pack) {
    this.pack = pack;
  }

  /**
   * Returns the values.
   *
   * @return the values value
   * @since 0.1.0
   */
  public Map<String, Object> getValues() {
    return values;
  }

  /**
   * Sets the values.
   *
   * @param values the values to set
   * @since 0.1.0
   */
  public void setValues(Map<String, Object> values) {
    this.values = values;
  }

  /**
   * Compares this resource with another object for equality based on the identifier.
   *
   * @param o the object to compare with
   * @return true if the identifiers match, false otherwise
   * @since 0.1.0
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Config other = (Config) o;
    return id != null && id.equals(other.getId());
  }

  /**
   * Returns a hash code value for this resource based on its identifier.
   *
   * @return the hash code
   * @since 0.1.0
   */
  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
