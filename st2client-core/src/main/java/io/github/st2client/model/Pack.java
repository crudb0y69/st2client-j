package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm pack, which is a collection of related actions, workflows, sensors, and
 * rules.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Pack extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("ref")
  private String ref;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("version")
  private String version;

  @JsonProperty("author")
  private String author;

  @JsonProperty("keywords")
  private List<String> keywords;

  @JsonProperty("email")
  private String email;

  @JsonProperty("system")
  private Map<String, Object> system;

  @JsonProperty("files")
  private List<String> files;

  @JsonProperty("dependencies")
  private List<String> dependencies;

  @JsonProperty("stackstorm_version")
  private String stackstormVersion;

  @JsonProperty("cert")
  private String cert;

  @JsonProperty("chatops")
  private Map<String, Object> chatops;

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
   * Returns the version.
   *
   * @return the version value
   * @since 0.1.0
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the version.
   *
   * @param version the version to set
   * @since 0.1.0
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Returns the author.
   *
   * @return the author value
   * @since 0.1.0
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Sets the author.
   *
   * @param author the author to set
   * @since 0.1.0
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Returns the keywords.
   *
   * @return the keywords value
   * @since 0.1.0
   */
  public List<String> getKeywords() {
    return keywords;
  }

  /**
   * Sets the keywords.
   *
   * @param keywords the keywords to set
   * @since 0.1.0
   */
  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
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
   * Returns the system metadata map from StackStorm ({@code {}} for a normal pack).
   *
   * @return the system map, or {@code null} if unset
   * @since 0.1.0
   */
  public Map<String, Object> getSystem() {
    return system;
  }

  /**
   * Sets the system metadata map.
   *
   * @param system the system map to set
   * @since 0.1.0
   */
  public void setSystem(Map<String, Object> system) {
    this.system = system;
  }

  /**
   * Returns the files.
   *
   * @return the files value
   * @since 0.1.0
   */
  public List<String> getFiles() {
    return files;
  }

  /**
   * Sets the files.
   *
   * @param files the files to set
   * @since 0.1.0
   */
  public void setFiles(List<String> files) {
    this.files = files;
  }

  /**
   * Returns the dependencies.
   *
   * @return the dependencies value
   * @since 0.1.0
   */
  public List<String> getDependencies() {
    return dependencies;
  }

  /**
   * Sets the dependencies.
   *
   * @param dependencies the dependencies to set
   * @since 0.1.0
   */
  public void setDependencies(List<String> dependencies) {
    this.dependencies = dependencies;
  }

  /**
   * Returns the stackstorm version.
   *
   * @return the stackstorm version value
   * @since 0.1.0
   */
  public String getStackstormVersion() {
    return stackstormVersion;
  }

  /**
   * Sets the stackstorm version.
   *
   * @param stackstormVersion the stackstorm version to set
   * @since 0.1.0
   */
  public void setStackstormVersion(String stackstormVersion) {
    this.stackstormVersion = stackstormVersion;
  }

  /**
   * Returns the cert.
   *
   * @return the cert value
   * @since 0.1.0
   */
  public String getCert() {
    return cert;
  }

  /**
   * Sets the cert.
   *
   * @param cert the cert to set
   * @since 0.1.0
   */
  public void setCert(String cert) {
    this.cert = cert;
  }

  /**
   * Returns the chatops.
   *
   * @return the chatops value
   * @since 0.1.0
   */
  public Map<String, Object> getChatops() {
    return chatops;
  }

  /**
   * Sets the chatops.
   *
   * @param chatops the chatops to set
   * @since 0.1.0
   */
  public void setChatops(Map<String, Object> chatops) {
    this.chatops = chatops;
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
    Pack other = (Pack) o;
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
