package io.github.st2client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm key-value pair, used for storing and retrieving arbitrary string data in
 * the datastore.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class KeyValuePair extends Resource {

  @JsonProperty("name")
  private String name;

  @JsonProperty("value")
  private String value;

  @JsonProperty("secret")
  private Boolean secret;

  @JsonProperty("encrypt")
  private Boolean encrypt;

  @JsonProperty("scope")
  private String scope;

  @JsonProperty("uid")
  private String uid;

  @JsonProperty("description")
  private String description;

  @JsonProperty("encrypted")
  private Boolean encrypted;

  @JsonProperty("expire_timestamp")
  private String expireTimestamp;

  @JsonProperty("ttl")
  private Integer ttl;

  @JsonProperty("id")
  private String id;

  /**
   * Enumeration of Scope values.
   *
   * @since 0.1.0
   */
  public enum Scope {
    SYSTEM("system"),
    USER("user");

    private final String value;

    Scope(String value) {
      this.value = value;
    }

    /**
     * Returns the value.
     *
     * @return the value value
     * @since 0.1.0
     */
    @com.fasterxml.jackson.annotation.JsonValue
    public String getValue() {
      return value;
    }

    /**
     * Creates a Scope enum from its string value.
     *
     * @param value the string value
     * @return the matching Scope enum, or null if not found
     * @since 0.1.0
     */
    @JsonCreator
    public static Scope fromValue(String value) {
      for (Scope s : values()) {
        if (s.value.equalsIgnoreCase(value)) return s;
      }
      return null;
    }
  }

  /**
   * Returns the key.
   *
   * @return the key value
   * @since 0.1.0
   */
  @JsonIgnore
  public String getKey() {
    return name;
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
   * Returns the value.
   *
   * @return the value value
   * @since 0.1.0
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the value to set
   * @since 0.1.0
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Returns the secret.
   *
   * @return the secret value
   * @since 0.1.0
   */
  public Boolean getSecret() {
    return secret;
  }

  /**
   * Sets the secret.
   *
   * @param secret the secret to set
   * @since 0.1.0
   */
  public void setSecret(Boolean secret) {
    this.secret = secret;
  }

  /**
   * Returns the encrypt.
   *
   * @return the encrypt value
   * @since 0.1.0
   */
  public Boolean getEncrypt() {
    return encrypt;
  }

  /**
   * Sets the encrypt.
   *
   * @param encrypt the encrypt to set
   * @since 0.1.0
   */
  public void setEncrypt(Boolean encrypt) {
    this.encrypt = encrypt;
  }

  /**
   * Returns the scope.
   *
   * @return the scope value
   * @since 0.1.0
   */
  public String getScope() {
    return scope;
  }

  /**
   * Sets the scope.
   *
   * @param scope the scope to set
   * @since 0.1.0
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  /**
   * Returns the scope enum.
   *
   * @return the scope enum value
   * @since 0.1.0
   */
  public Scope getScopeEnum() {
    return Scope.fromValue(scope);
  }

  /**
   * Sets the scope enum.
   *
   * @param scopeEnum the scope enum to set
   * @since 0.1.0
   */
  public void setScopeEnum(Scope scopeEnum) {
    this.scope = scopeEnum != null ? scopeEnum.getValue() : null;
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
   * Returns the encrypted.
   *
   * @return the encrypted value
   * @since 0.1.0
   */
  public Boolean getEncrypted() {
    return encrypted;
  }

  /**
   * Sets the encrypted.
   *
   * @param encrypted the encrypted to set
   * @since 0.1.0
   */
  public void setEncrypted(Boolean encrypted) {
    this.encrypted = encrypted;
  }

  /**
   * Returns the expire timestamp.
   *
   * @return the expire timestamp value
   * @since 0.1.0
   */
  public String getExpireTimestamp() {
    return expireTimestamp;
  }

  /**
   * Sets the expire timestamp.
   *
   * @param expireTimestamp the expire timestamp to set
   * @since 0.1.0
   */
  public void setExpireTimestamp(String expireTimestamp) {
    this.expireTimestamp = expireTimestamp;
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
   * Returns a string representation of this key-value pair.
   *
   * @return a masked string showing the name but hiding the value
   * @since 0.1.0
   */
  @Override
  public String toString() {
    return "KeyValuePair name=" + name + " value=***";
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
    KeyValuePair other = (KeyValuePair) o;
    return name != null && name.equals(other.getName());
  }

  /**
   * Returns a hash code value for this instance.
   *
   * @return the hash code
   * @since 0.1.0
   */
  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
