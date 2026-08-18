package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm trigger type, defining the schema and metadata for a category of
 * triggers.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class TriggerType extends Resource {

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

  @JsonProperty("payload_schema")
  private Map<String, Object> payloadSchema;

  @JsonProperty("parameters_schema")
  private Map<String, Object> parametersSchema;

  @JsonProperty("tags")
  private List<String> tags;

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
   * Returns the payload schema.
   *
   * @return the payload schema value
   * @since 0.1.0
   */
  public Map<String, Object> getPayloadSchema() {
    return payloadSchema;
  }

  /**
   * Sets the payload schema.
   *
   * @param payloadSchema the payload schema to set
   * @since 0.1.0
   */
  public void setPayloadSchema(Map<String, Object> payloadSchema) {
    this.payloadSchema = payloadSchema;
  }

  /**
   * Returns the parameters schema.
   *
   * @return the parameters schema value
   * @since 0.1.0
   */
  public Map<String, Object> getParametersSchema() {
    return parametersSchema;
  }

  /**
   * Sets the parameters schema.
   *
   * @param parametersSchema the parameters schema to set
   * @since 0.1.0
   */
  public void setParametersSchema(Map<String, Object> parametersSchema) {
    this.parametersSchema = parametersSchema;
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
    TriggerType other = (TriggerType) o;
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
