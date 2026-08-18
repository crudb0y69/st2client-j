package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm inquiry, which is a paused action execution awaiting user input or
 * approval.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Inquiry extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("route")
  private String route;

  @JsonProperty("ttl")
  private Integer ttl;

  @JsonProperty("users")
  private List<String> users;

  @JsonProperty("roles")
  private List<String> roles;

  @JsonProperty("schema")
  private Map<String, Object> schema;

  @JsonProperty("liveaction")
  private Map<String, Object> liveaction;

  @JsonProperty("runner")
  private Map<String, Object> runner;

  @JsonProperty("status")
  private String status;

  @JsonProperty("parent")
  private String parent;

  @JsonProperty("result")
  private Object result;

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
   * Returns the route.
   *
   * @return the route value
   * @since 0.1.0
   */
  public String getRoute() {
    return route;
  }

  /**
   * Sets the route.
   *
   * @param route the route to set
   * @since 0.1.0
   */
  public void setRoute(String route) {
    this.route = route;
  }

  /**
   * Returns the ttl.
   *
   * @return the ttl value
   * @since 0.1.0
   */
  public Integer getTtl() {
    return ttl;
  }

  /**
   * Sets the ttl.
   *
   * @param ttl the ttl to set
   * @since 0.1.0
   */
  public void setTtl(Integer ttl) {
    this.ttl = ttl;
  }

  /**
   * Returns the users.
   *
   * @return the users value
   * @since 0.1.0
   */
  public List<String> getUsers() {
    return users;
  }

  /**
   * Sets the users.
   *
   * @param users the users to set
   * @since 0.1.0
   */
  public void setUsers(List<String> users) {
    this.users = users;
  }

  /**
   * Returns the roles.
   *
   * @return the roles value
   * @since 0.1.0
   */
  public List<String> getRoles() {
    return roles;
  }

  /**
   * Sets the roles.
   *
   * @param roles the roles to set
   * @since 0.1.0
   */
  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  /**
   * Returns the schema.
   *
   * @return the schema value
   * @since 0.1.0
   */
  public Map<String, Object> getSchema() {
    return schema;
  }

  /**
   * Sets the schema.
   *
   * @param schema the schema to set
   * @since 0.1.0
   */
  public void setSchema(Map<String, Object> schema) {
    this.schema = schema;
  }

  /**
   * Returns the liveaction.
   *
   * @return the liveaction value
   * @since 0.1.0
   */
  public Map<String, Object> getLiveaction() {
    return liveaction;
  }

  /**
   * Sets the liveaction.
   *
   * @param liveaction the liveaction to set
   * @since 0.1.0
   */
  public void setLiveaction(Map<String, Object> liveaction) {
    this.liveaction = liveaction;
  }

  /**
   * Returns the runner.
   *
   * @return the runner value
   * @since 0.1.0
   */
  public Map<String, Object> getRunner() {
    return runner;
  }

  /**
   * Sets the runner.
   *
   * @param runner the runner to set
   * @since 0.1.0
   */
  public void setRunner(Map<String, Object> runner) {
    this.runner = runner;
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
   * Returns the parent.
   *
   * @return the parent value
   * @since 0.1.0
   */
  public String getParent() {
    return parent;
  }

  /**
   * Sets the parent.
   *
   * @param parent the parent to set
   * @since 0.1.0
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Returns the result.
   *
   * @return the result value
   * @since 0.1.0
   */
  public Object getResult() {
    return result;
  }

  /**
   * Sets the result.
   *
   * @param result the result to set
   * @since 0.1.0
   */
  public void setResult(Object result) {
    this.result = result;
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
    Inquiry other = (Inquiry) o;
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
