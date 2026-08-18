package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm trigger instance, capturing a single occurrence of a trigger with its
 * payload and timing.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class TriggerInstance extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("trigger")
  private String trigger;

  @JsonProperty("occurrence_time")
  private String occurrenceTime;

  @JsonProperty("payload")
  private Map<String, Object> payload;

  @JsonProperty("status")
  private String status;

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
   * Returns the trigger.
   *
   * @return the trigger value
   * @since 0.1.0
   */
  public String getTrigger() {
    return trigger;
  }

  /**
   * Sets the trigger.
   *
   * @param trigger the trigger to set
   * @since 0.1.0
   */
  public void setTrigger(String trigger) {
    this.trigger = trigger;
  }

  /**
   * Returns the occurrence time.
   *
   * @return the occurrence time value
   * @since 0.1.0
   */
  public String getOccurrenceTime() {
    return occurrenceTime;
  }

  /**
   * Sets the occurrence time.
   *
   * @param occurrenceTime the occurrence time to set
   * @since 0.1.0
   */
  public void setOccurrenceTime(String occurrenceTime) {
    this.occurrenceTime = occurrenceTime;
  }

  /**
   * Returns the payload.
   *
   * @return the payload value
   * @since 0.1.0
   */
  public Map<String, Object> getPayload() {
    return payload;
  }

  /**
   * Sets the payload.
   *
   * @param payload the payload to set
   * @since 0.1.0
   */
  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  /**
   * Returns the status.
   *
   * @return the status value
   * @since 0.1.0
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status.
   *
   * @param status the status to set
   * @since 0.1.0
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Compares this instance with another object for equality based on the identifier.
   *
   * @param o the object to compare with
   * @return true if the identifiers match, false otherwise
   * @since 0.1.0
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TriggerInstance other = (TriggerInstance) o;
    return id != null && id.equals(other.getId());
  }

  /**
   * Returns a hash code value for this instance.
   *
   * @return the hash code
   * @since 0.1.0
   */
  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
