package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm trace, which tracks the end-to-end flow across trigger instances, rule
 * enforcements, and action executions.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Trace extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("trace_tag")
  private String traceTag;

  @JsonProperty("start_timestamp")
  private String startTimestamp;

  @JsonProperty("end_timestamp")
  private String endTimestamp;

  @JsonProperty("action_executions")
  private List<Map<String, Object>> actionExecutions;

  @JsonProperty("trigger_instances")
  private List<Map<String, Object>> triggerInstances;

  @JsonProperty("rule_enforcements")
  private List<Map<String, Object>> ruleEnforcements;

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
   * Returns the uid.
   *
   * @return the uid value
   * @since 0.1.0
   */
  public String getUid() {
    return uid;
  }

  /**
   * Sets the uid.
   *
   * @param uid the uid to set
   * @since 0.1.0
   */
  public void setUid(String uid) {
    this.uid = uid;
  }

  /**
   * Returns the trace tag.
   *
   * @return the trace tag value
   * @since 0.1.0
   */
  public String getTraceTag() {
    return traceTag;
  }

  /**
   * Sets the trace tag.
   *
   * @param traceTag the trace tag to set
   * @since 0.1.0
   */
  public void setTraceTag(String traceTag) {
    this.traceTag = traceTag;
  }

  /**
   * Returns the start timestamp.
   *
   * @return the start timestamp value
   * @since 0.1.0
   */
  public String getStartTimestamp() {
    return startTimestamp;
  }

  /**
   * Sets the start timestamp.
   *
   * @param startTimestamp the start timestamp to set
   * @since 0.1.0
   */
  public void setStartTimestamp(String startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  /**
   * Returns the end timestamp.
   *
   * @return the end timestamp value
   * @since 0.1.0
   */
  public String getEndTimestamp() {
    return endTimestamp;
  }

  /**
   * Sets the end timestamp.
   *
   * @param endTimestamp the end timestamp to set
   * @since 0.1.0
   */
  public void setEndTimestamp(String endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  /**
   * Returns the action executions.
   *
   * @return the action executions value
   * @since 0.1.0
   */
  public List<Map<String, Object>> getActionExecutions() {
    return actionExecutions;
  }

  /**
   * Sets the action executions.
   *
   * @param actionExecutions the action executions to set
   * @since 0.1.0
   */
  public void setActionExecutions(List<Map<String, Object>> actionExecutions) {
    this.actionExecutions = actionExecutions;
  }

  /**
   * Returns the trigger instances.
   *
   * @return the trigger instances value
   * @since 0.1.0
   */
  public List<Map<String, Object>> getTriggerInstances() {
    return triggerInstances;
  }

  /**
   * Sets the trigger instances.
   *
   * @param triggerInstances the trigger instances to set
   * @since 0.1.0
   */
  public void setTriggerInstances(List<Map<String, Object>> triggerInstances) {
    this.triggerInstances = triggerInstances;
  }

  /**
   * Returns the rule enforcements.
   *
   * @return the rule enforcements value
   * @since 0.1.0
   */
  public List<Map<String, Object>> getRuleEnforcements() {
    return ruleEnforcements;
  }

  /**
   * Sets the rule enforcements.
   *
   * @param ruleEnforcements the rule enforcements to set
   * @since 0.1.0
   */
  public void setRuleEnforcements(List<Map<String, Object>> ruleEnforcements) {
    this.ruleEnforcements = ruleEnforcements;
  }

  /**
   * Returns a string representation of this trace.
   *
   * @return a string containing the identifier and trace tag
   * @since 0.1.0
   */
  @Override
  public String toString() {
    return "Trace id="
        + id
        + " traceTag="
        + traceTag
        + " startTimestamp="
        + startTimestamp
        + " endTimestamp="
        + endTimestamp;
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
    Trace other = (Trace) o;
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
