package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm API key used for authentication and authorization of API requests.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ApiKey extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("user")
  private String user;

  @JsonProperty("key_hash")
  private String keyHash;

  @JsonProperty("metadata")
  private Map<String, Object> metadata;

  @JsonProperty("created_at")
  private String createdAt;

  @JsonProperty("enabled")
  private Boolean enabled;

  @JsonProperty("key")
  private String key;

  @JsonProperty("description")
  private String description;

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
   * Returns the user.
   *
   * @return the user value
   * @since 0.1.0
   */
  public String getUser() {
    return user;
  }

  /**
   * Sets the user.
   *
   * @param user the user to set
   * @since 0.1.0
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * Returns the key hash.
   *
   * @return the key hash value
   * @since 0.1.0
   */
  public String getKeyHash() {
    return keyHash;
  }

  /**
   * Sets the key hash.
   *
   * @param keyHash the key hash to set
   * @since 0.1.0
   */
  public void setKeyHash(String keyHash) {
    this.keyHash = keyHash;
  }

  /**
   * Returns the metadata.
   *
   * @return the metadata value
   * @since 0.1.0
   */
  public Map<String, Object> getMetadata() {
    return metadata;
  }

  /**
   * Sets the metadata.
   *
   * @param metadata the metadata to set
   * @since 0.1.0
   */
  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  /**
   * Returns the created at.
   *
   * @return the created at value
   * @since 0.1.0
   */
  public String getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the created at.
   *
   * @param createdAt the created at to set
   * @since 0.1.0
   */
  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
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
   * Returns the key.
   *
   * @return the key value
   * @since 0.1.0
   */
  public String getKey() {
    return key;
  }

  /**
   * Sets the key.
   *
   * @param key the key to set
   * @since 0.1.0
   */
  public void setKey(String key) {
    this.key = key;
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
    ApiKey other = (ApiKey) o;
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
