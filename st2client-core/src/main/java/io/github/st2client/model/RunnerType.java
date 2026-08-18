package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm runner type, which defines the execution environment and parameter schema
 * for a category of actions.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class RunnerType extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("name")
  private String name;

  @JsonProperty("enabled")
  private Boolean enabled;

  @JsonProperty("description")
  private String description;

  @JsonProperty("runner_module")
  private String runnerModule;

  @JsonProperty("runner_parameters")
  private Map<String, Object> runnerParameters;

  @JsonProperty("query_module")
  private String queryModule;

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
   * Returns the name.
   *
   * @return the name value
   * @since 0.1.0
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the name to set
   * @since 0.1.0
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the enabled.
   *
   * @return the enabled value
   * @since 0.1.0
   */
  public Boolean getEnabled() {
    return enabled;
  }

  /**
   * Sets the enabled.
   *
   * @param enabled the enabled to set
   * @since 0.1.0
   */
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Returns the description.
   *
   * @return the description value
   * @since 0.1.0
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the description to set
   * @since 0.1.0
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the runner module.
   *
   * @return the runner module value
   * @since 0.1.0
   */
  public String getRunnerModule() {
    return runnerModule;
  }

  /**
   * Sets the runner module.
   *
   * @param runnerModule the runner module to set
   * @since 0.1.0
   */
  public void setRunnerModule(String runnerModule) {
    this.runnerModule = runnerModule;
  }

  /**
   * Returns the runner parameters.
   *
   * @return the runner parameters value
   * @since 0.1.0
   */
  public Map<String, Object> getRunnerParameters() {
    return runnerParameters;
  }

  /**
   * Sets the runner parameters.
   *
   * @param runnerParameters the runner parameters to set
   * @since 0.1.0
   */
  public void setRunnerParameters(Map<String, Object> runnerParameters) {
    this.runnerParameters = runnerParameters;
  }

  /**
   * Returns the query module.
   *
   * @return the query module value
   * @since 0.1.0
   */
  public String getQueryModule() {
    return queryModule;
  }

  /**
   * Sets the query module.
   *
   * @param queryModule the query module to set
   * @since 0.1.0
   */
  public void setQueryModule(String queryModule) {
    this.queryModule = queryModule;
  }

  /**
   * Returns a string representation of this runner type.
   *
   * @return a string containing the identifier, name, and enabled status
   * @since 0.1.0
   */
  @Override
  public String toString() {
    return "RunnerType id="
        + id
        + " name="
        + name
        + " enabled="
        + enabled
        + " runnerModule="
        + runnerModule;
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
    RunnerType other = (RunnerType) o;
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
