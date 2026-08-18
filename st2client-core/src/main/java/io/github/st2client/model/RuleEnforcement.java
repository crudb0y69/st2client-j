package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm rule enforcement, recording the execution history when a rule was
 * triggered and an action was invoked.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class RuleEnforcement extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("trigger_instance_id")
  private String triggerInstanceId;

  @JsonProperty("execution_id")
  private String executionId;

  @JsonProperty("rule")
  private Map<String, Object> rule;

  @JsonProperty("enforced_at")
  private String enforcedAt;

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
   * Returns the trigger instance id.
   *
   * @return the trigger instance id value
   * @since 0.1.0
   */
  public String getTriggerInstanceId() {
    return triggerInstanceId;
  }

  /**
   * Sets the trigger instance id.
   *
   * @param triggerInstanceId the trigger instance id to set
   * @since 0.1.0
   */
  public void setTriggerInstanceId(String triggerInstanceId) {
    this.triggerInstanceId = triggerInstanceId;
  }

  /**
   * Returns the execution id.
   *
   * @return the execution id value
   * @since 0.1.0
   */
  public String getExecutionId() {
    return executionId;
  }

  /**
   * Sets the execution id.
   *
   * @param executionId the execution id to set
   * @since 0.1.0
   */
  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  /**
   * Returns the rule.
   *
   * @return the rule value
   * @since 0.1.0
   */
  public Map<String, Object> getRule() {
    return rule;
  }

  /**
   * Sets the rule.
   *
   * @param rule the rule to set
   * @since 0.1.0
   */
  public void setRule(Map<String, Object> rule) {
    this.rule = rule;
  }

  /**
   * Returns the enforced at.
   *
   * @return the enforced at value
   * @since 0.1.0
   */
  public String getEnforcedAt() {
    return enforcedAt;
  }

  /**
   * Sets the enforced at.
   *
   * @param enforcedAt the enforced at to set
   * @since 0.1.0
   */
  public void setEnforcedAt(String enforcedAt) {
    this.enforcedAt = enforcedAt;
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
    RuleEnforcement other = (RuleEnforcement) o;
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
