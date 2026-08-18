package io.github.st2client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm authentication token used for API access and session management.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Token extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("user")
  private String user;

  @JsonProperty("token")
  private String token;

  @JsonProperty("expiry")
  private String expiry;

  @JsonProperty("metadata")
  private Map<String, Object> metadata;

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
   * Returns the token.
   *
   * @return the token value
   * @since 0.1.0
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets the token.
   *
   * @param token the token to set
   * @since 0.1.0
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Returns the expiry.
   *
   * @return the expiry value
   * @since 0.1.0
   */
  public String getExpiry() {
    return expiry;
  }

  /**
   * Sets the expiry.
   *
   * @param expiry the expiry to set
   * @since 0.1.0
   */
  public void setExpiry(String expiry) {
    this.expiry = expiry;
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
   * Returns a string representation of this token.
   *
   * @return a masked string showing the user but hiding the token value
   * @since 0.1.0
   */
  @Override
  public String toString() {
    return "Token id=" + id + " user=" + user + " token=***";
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
    Token other = (Token) o;
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
