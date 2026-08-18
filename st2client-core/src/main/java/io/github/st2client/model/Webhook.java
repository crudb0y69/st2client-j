package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm webhook configuration for receiving external HTTP requests and converting
 * them into triggers.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Webhook extends Resource {

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

  @JsonProperty("type")
  private String type;

  @JsonProperty("parameters")
  private Map<String, Object> parameters;

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
    Webhook other = (Webhook) o;
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
