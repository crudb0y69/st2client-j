package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm action alias, providing alternative invocation patterns and format-based
 * matching for actions.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ActionAlias extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("ref")
  private String ref;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("name")
  private String name;

  @JsonProperty("pack")
  private String pack;

  @JsonProperty("description")
  private String description;

  @JsonProperty("enabled")
  private Boolean enabled;

  @JsonProperty("action_ref")
  private String actionRef;

  @JsonProperty("formats")
  private List<Object> formats;

  @JsonProperty("ack")
  private Map<String, Object> ack;

  @JsonProperty("result")
  private Map<String, Object> result;

  @JsonProperty("extra")
  private Map<String, Object> extra;

  @JsonProperty("immutable_parameters")
  private Map<String, Object> immutableParameters;

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
   * Returns the action ref.
   *
   * @return the action ref value
   * @since 0.1.0
   */
  public String getActionRef() {
    return actionRef;
  }

  /**
   * Sets the action ref.
   *
   * @param actionRef the action ref to set
   * @since 0.1.0
   */
  public void setActionRef(String actionRef) {
    this.actionRef = actionRef;
  }

  /**
   * Returns the formats.
   *
   * @return the formats value
   * @since 0.1.0
   */
  public List<Object> getFormats() {
    return formats;
  }

  /**
   * Sets the formats.
   *
   * @param formats the formats to set
   * @since 0.1.0
   */
  public void setFormats(List<Object> formats) {
    this.formats = formats;
  }

  /**
   * Returns the ack.
   *
   * @return the ack value
   * @since 0.1.0
   */
  public Map<String, Object> getAck() {
    return ack;
  }

  /**
   * Sets the ack.
   *
   * @param ack the ack to set
   * @since 0.1.0
   */
  public void setAck(Map<String, Object> ack) {
    this.ack = ack;
  }

  /**
   * Returns the result.
   *
   * @return the result value
   * @since 0.1.0
   */
  public Map<String, Object> getResult() {
    return result;
  }

  /**
   * Sets the result.
   *
   * @param result the result to set
   * @since 0.1.0
   */
  public void setResult(Map<String, Object> result) {
    this.result = result;
  }

  /**
   * Returns the extra.
   *
   * @return the extra value
   * @since 0.1.0
   */
  public Map<String, Object> getExtra() {
    return extra;
  }

  /**
   * Sets the extra.
   *
   * @param extra the extra to set
   * @since 0.1.0
   */
  public void setExtra(Map<String, Object> extra) {
    this.extra = extra;
  }

  /**
   * Returns the immutable parameters.
   *
   * @return the immutable parameters value
   * @since 0.1.0
   */
  public Map<String, Object> getImmutableParameters() {
    return immutableParameters;
  }

  /**
   * Sets the immutable parameters.
   *
   * @param immutableParameters the immutable parameters to set
   * @since 0.1.0
   */
  public void setImmutableParameters(Map<String, Object> immutableParameters) {
    this.immutableParameters = immutableParameters;
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
    ActionAlias other = (ActionAlias) o;
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
