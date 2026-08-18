package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm sensor, which is a Python plugin that monitors external systems and
 * generates triggers.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Sensor extends Resource {

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

  @JsonProperty("class_name")
  private String className;

  @JsonProperty("artifact_uri")
  private String artifactUri;

  @JsonProperty("entry_point")
  private String entryPoint;

  @JsonProperty("enabled")
  private Boolean enabled;

  @JsonProperty("trigger_types")
  private List<Object> triggerTypes;

  @JsonProperty("poll_interval")
  private Double pollInterval;

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
   * Returns the class name.
   *
   * @return the class name value
   * @since 0.1.0
   */
  public String getClassName() {
    return className;
  }

  /**
   * Sets the class name.
   *
   * @param className the class name to set
   * @since 0.1.0
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * Returns the artifact uri.
   *
   * @return the artifact uri value
   * @since 0.1.0
   */
  public String getArtifactUri() {
    return artifactUri;
  }

  /**
   * Sets the artifact uri.
   *
   * @param artifactUri the artifact uri to set
   * @since 0.1.0
   */
  public void setArtifactUri(String artifactUri) {
    this.artifactUri = artifactUri;
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
   * Returns the trigger types.
   *
   * @return the trigger types value
   * @since 0.1.0
   */
  public List<Object> getTriggerTypes() {
    return triggerTypes;
  }

  /**
   * Sets the trigger types.
   *
   * @param triggerTypes the trigger types to set
   * @since 0.1.0
   */
  public void setTriggerTypes(List<Object> triggerTypes) {
    this.triggerTypes = triggerTypes;
  }

  /**
   * Returns the poll interval.
   *
   * @return the poll interval value
   * @since 0.1.0
   */
  public Double getPollInterval() {
    return pollInterval;
  }

  /**
   * Sets the poll interval.
   *
   * @param pollInterval the poll interval to set
   * @since 0.1.0
   */
  public void setPollInterval(Double pollInterval) {
    this.pollInterval = pollInterval;
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
    Sensor other = (Sensor) o;
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
