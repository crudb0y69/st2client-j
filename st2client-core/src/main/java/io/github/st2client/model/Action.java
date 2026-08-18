package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm action, which defines an executable operation that can be invoked via the
 * action runner framework.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Action extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("ref")
  private String ref;

  @JsonProperty("name")
  private String name;

  @JsonProperty("pack")
  private String pack;

  @JsonProperty("description")
  private String description;

  @JsonProperty("enabled")
  private Boolean enabled;

  @JsonProperty("runner_type")
  private String runnerType;

  @JsonProperty("entry_point")
  private String entryPoint;

  @JsonProperty("parameters")
  private Map<String, Object> parameters;

  @JsonProperty("tags")
  private List<String> tags;

  @JsonProperty("notify")
  private Map<String, Object> notify;

  @JsonProperty("output_schema")
  private Map<String, Object> outputSchema;

  @JsonProperty("args")
  private Map<String, Object> args;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("type")
  private String type;

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
   * Returns the ref.
   *
   * @return the ref value
   * @since 0.1.0
   */
  public String getRef() {
    return ref;
  }

  /**
   * Sets the ref.
   *
   * @param ref the ref to set
   * @since 0.1.0
   */
  public void setRef(String ref) {
    this.ref = ref;
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
   * Returns the runner type.
   *
   * @return the runner type value
   * @since 0.1.0
   */
  public String getRunnerType() {
    return runnerType;
  }

  /**
   * Sets the runner type.
   *
   * @param runnerType the runner type to set
   * @since 0.1.0
   */
  public void setRunnerType(String runnerType) {
    this.runnerType = runnerType;
  }

  /**
   * Returns the entry point.
   *
   * @return the entry point value
   * @since 0.1.0
   */
  public String getEntryPoint() {
    return entryPoint;
  }

  /**
   * Sets the entry point.
   *
   * @param entryPoint the entry point to set
   * @since 0.1.0
   */
  public void setEntryPoint(String entryPoint) {
    this.entryPoint = entryPoint;
  }

  /**
   * Returns the parameters.
   *
   * @return the parameters value
   * @since 0.1.0
   */
  public Map<String, Object> getParameters() {
    return parameters;
  }

  /**
   * Sets the parameters.
   *
   * @param parameters the parameters to set
   * @since 0.1.0
   */
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  /**
   * Returns the tags.
   *
   * @return the tags value
   * @since 0.1.0
   */
  public List<String> getTags() {
    return tags;
  }

  /**
   * Sets the tags.
   *
   * @param tags the tags to set
   * @since 0.1.0
   */
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   * Returns to notify.
   *
   * @return the notify value
   * @since 0.1.0
   */
  public Map<String, Object> getNotify() {
    return notify;
  }

  /**
   * Sets to notify.
   *
   * @param notify the notify to set
   * @since 0.1.0
   */
  public void setNotify(Map<String, Object> notify) {
    this.notify = notify;
  }

  /**
   * Returns the output schema.
   *
   * @return the output schema value
   * @since 0.1.0
   */
  public Map<String, Object> getOutputSchema() {
    return outputSchema;
  }

  /**
   * Sets the output schema.
   *
   * @param outputSchema the output schema to set
   * @since 0.1.0
   */
  public void setOutputSchema(Map<String, Object> outputSchema) {
    this.outputSchema = outputSchema;
  }

  /**
   * Returns the args.
   *
   * @return the args value
   * @since 0.1.0
   */
  public Map<String, Object> getArgs() {
    return args;
  }

  /**
   * Sets the args.
   *
   * @param args the args to set
   * @since 0.1.0
   */
  public void setArgs(Map<String, Object> args) {
    this.args = args;
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
   * Returns the type.
   *
   * @return the type value
   * @since 0.1.0
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the type to set
   * @since 0.1.0
   */
  public void setType(String type) {
    this.type = type;
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
    Action other = (Action) o;
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

  /**
   * Builder for creating Action instances.
   *
   * @since 0.1.0
   */
  public static class Builder {
    private final String name;
    private final String pack;
    private String id;
    private String ref;
    private Boolean enabled;
    private String runnerType;
    private String entryPoint;
    private Map<String, Object> parameters;
    private List<String> tags;
    private Map<String, Object> notify;
    private Map<String, Object> outputSchema;
    private Map<String, Object> args;
    private String uid;
    private String type;
    private String description;

    /**
     * Constructs a new Builder with the required fields.
     *
     * @param name the name
     * @param pack the pack
     * @since 0.1.0
     */
    public Builder(String name, String pack) {
      this.name = java.util.Objects.requireNonNull(name, "name must not be null");
      this.pack = java.util.Objects.requireNonNull(pack, "pack must not be null");
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the ref.
     *
     * @param ref the ref to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder ref(String ref) {
      this.ref = ref;
      return this;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled the enabled to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder enabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    /**
     * Sets the runner type.
     *
     * @param runnerType the runner type to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder runnerType(String runnerType) {
      this.runnerType = runnerType;
      return this;
    }

    /**
     * Sets the entry point.
     *
     * @param entryPoint the entry point to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder entryPoint(String entryPoint) {
      this.entryPoint = entryPoint;
      return this;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the parameters to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder parameters(Map<String, Object> parameters) {
      this.parameters = parameters;
      return this;
    }

    /**
     * Sets the tags.
     *
     * @param tags the tags to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder tags(List<String> tags) {
      this.tags = tags;
      return this;
    }

    /**
     * Sets to notify.
     *
     * @param notify the notify to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder notify(Map<String, Object> notify) {
      this.notify = notify;
      return this;
    }

    /**
     * Sets the output schema.
     *
     * @param outputSchema the output schema to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder outputSchema(Map<String, Object> outputSchema) {
      this.outputSchema = outputSchema;
      return this;
    }

    /**
     * Sets the args.
     *
     * @param args the args to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder args(Map<String, Object> args) {
      this.args = args;
      return this;
    }

    /**
     * Sets the uid.
     *
     * @param uid the uid to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder uid(String uid) {
      this.uid = uid;
      return this;
    }

    /**
     * Sets the type.
     *
     * @param type the type to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder type(String type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Builds the resource instance with all configured properties.
     *
     * @return the constructed resource instance
     * @since 0.1.0
     */
    public Action build() {
      Action action = new Action();
      action.setName(name);
      action.setPack(pack);
      action.setId(id);
      action.setRef(ref);
      action.setEnabled(enabled);
      action.setRunnerType(runnerType);
      action.setEntryPoint(entryPoint);
      action.setParameters(parameters);
      action.setTags(tags);
      action.setNotify(notify);
      action.setOutputSchema(outputSchema);
      action.setArgs(args);
      action.setUid(uid);
      action.setType(type);
      action.setDescription(description);
      return action;
    }
  }
}
