package io.github.st2client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents user information returned by the StackStorm /users endpoint, including roles and
 * authentication details.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class UserInfo extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("username")
  private String username;

  @JsonProperty("email")
  private String email;

  @JsonProperty("roles")
  private List<String> roles;

  @JsonProperty("is_admin")
  private Boolean isAdmin;

  /**
   * Returns the user identifier.
   *
   * @return the user id
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
   * Returns the username.
   *
   * @return the username value
   * @since 0.1.0
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username.
   *
   * @param username the username to set
   * @since 0.1.0
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the email.
   *
   * @return the email value
   * @since 0.1.0
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the email to set
   * @since 0.1.0
   */
  public void setEmail(String email) {
    this.email = email;
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
   * Returns the is admin.
   *
   * @return the is admin value
   * @since 0.1.0
   */
  public Boolean getIsAdmin() {
    return isAdmin;
  }

  /**
   * Sets the is admin.
   *
   * @param isAdmin the is admin to set
   * @since 0.1.0
   */
  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
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
    UserInfo other = (UserInfo) o;
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
