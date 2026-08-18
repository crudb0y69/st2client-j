package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm rule, which defines a mapping between a trigger and an action with
 * optional criteria for conditional matching.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Rule extends Resource {

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

  @JsonProperty("trigger")
  private Map<String, Object> trigger;

  @JsonProperty("criteria")
  private Object criteria;

  @JsonProperty("action")
  private Map<String, Object> action;

  @JsonProperty("tags")
  private java.util.List<String> tags;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("type")
  private Object type;

  @JsonProperty("context")
  private Map<String, Object> context;

  @JsonProperty("metadata_file")
  private String metadataFile;

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
   * Returns the trigger.
   *
   * @return the trigger value
   * @since 0.1.0
   */
  public Map<String, Object> getTrigger() {
    return trigger;
  }

  /**
   * Sets the trigger.
   *
   * @param trigger the trigger to set
   * @since 0.1.0
   */
  public void setTrigger(Map<String, Object> trigger) {
    this.trigger = trigger;
  }

  /**
   * Returns the criteria.
   *
   * @return the criteria value
   * @since 0.1.0
   */
  public Object getCriteria() {
    return criteria;
  }

  /**
   * Sets the criteria.
   *
   * @param criteria the criteria to set
   * @since 0.1.0
   */
  public void setCriteria(Object criteria) {
    this.criteria = criteria;
  }

  /**
   * Returns the action.
   *
   * @return the action value
   * @since 0.1.0
   */
  public Map<String, Object> getAction() {
    return action;
  }

  /**
   * Sets the action.
   *
   * @param action the action to set
   * @since 0.1.0
   */
  public void setAction(Map<String, Object> action) {
    this.action = action;
  }

  /**
   * Returns the tags.
   *
   * @return the tags value
   * @since 0.1.0
   */
  public java.util.List<String> getTags() {
    return tags;
  }

  /**
   * Sets the tags.
   *
   * @param tags the tags to set
   * @since 0.1.0
   */
  public void setTags(java.util.List<String> tags) {
    this.tags = tags;
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
  public Object getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the type to set
   * @since 0.1.0
   */
  public void setType(Object type) {
    this.type = type;
  }

  /**
   * Returns the context.
   *
   * @return the context value
   * @since 0.1.0
   */
  public Map<String, Object> getContext() {
    return context;
  }

  /**
   * Sets the context.
   *
   * @param context the context to set
   * @since 0.1.0
   */
  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  /**
   * Returns the metadata file.
   *
   * @return the metadata file value
   * @since 0.1.0
   */
  public String getMetadataFile() {
    return metadataFile;
  }

  /**
   * Sets the metadata file.
   *
   * @param metadataFile the metadata file to set
   * @since 0.1.0
   */
  public void setMetadataFile(String metadataFile) {
    this.metadataFile = metadataFile;
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
    Rule other = (Rule) o;
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

  /**
   * Builder for creating Rule instances.
   *
   * @since 0.1.0
   */
  public static class Builder {
    private final String name;
    private final String pack;
    private String id;
    private String ref;
    private Boolean enabled;
    private Map<String, Object> trigger;
    private Object criteria;
    private Map<String, Object> action;
    private java.util.List<String> tags;
    private String uid;
    private Object type;
    private Map<String, Object> context;
    private String metadataFile;
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
     * Sets the trigger.
     *
     * @param trigger the trigger to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder trigger(Map<String, Object> trigger) {
      this.trigger = trigger;
      return this;
    }

    /**
     * Sets the criteria.
     *
     * @param criteria the criteria to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder criteria(Object criteria) {
      this.criteria = criteria;
      return this;
    }

    /**
     * Sets the action.
     *
     * @param action the action to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder action(Map<String, Object> action) {
      this.action = action;
      return this;
    }

    /**
     * Sets the tags.
     *
     * @param tags the tags to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder tags(java.util.List<String> tags) {
      this.tags = tags;
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
    public Builder type(Object type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the context.
     *
     * @param context the context to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder context(Map<String, Object> context) {
      this.context = context;
      return this;
    }

    /**
     * Sets the metadata file.
     *
     * @param metadataFile the metadata file to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder metadataFile(String metadataFile) {
      this.metadataFile = metadataFile;
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
    public Rule build() {
      Rule rule = new Rule();
      rule.setName(name);
      rule.setPack(pack);
      rule.setId(id);
      rule.setRef(ref);
      rule.setEnabled(enabled);
      rule.setTrigger(trigger);
      rule.setCriteria(criteria);
      rule.setAction(action);
      rule.setTags(tags);
      rule.setUid(uid);
      rule.setType(type);
      rule.setContext(context);
      rule.setMetadataFile(metadataFile);
      rule.setDescription(description);
      return rule;
    }
  }
}
