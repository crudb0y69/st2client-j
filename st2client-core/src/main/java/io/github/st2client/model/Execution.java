package io.github.st2client.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a StackStorm action execution, capturing the invocation, status, parameters, and
 * results of a running or completed action.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class Execution extends Resource {

  @JsonProperty("id")
  private String id;

  @JsonProperty("action")
  private Map<String, Object> action;

  @JsonProperty("status")
  private String status;

  @JsonProperty("parameters")
  private Map<String, Object> parameters;

  @JsonProperty("result")
  private Object result;

  @JsonProperty("start_timestamp")
  private String startTimestamp;

  @JsonProperty("end_timestamp")
  private String endTimestamp;

  @JsonProperty("delay")
  private Integer delay;

  @JsonProperty("user")
  private String user;

  @JsonProperty("context")
  private Map<String, Object> context;

  @JsonProperty("liveaction")
  private Map<String, Object> liveaction;

  @JsonProperty("children")
  private List<String> children;

  @JsonProperty("parent")
  private String parent;

  @JsonProperty("depth")
  private Integer depth;

  @JsonProperty("task")
  private String task;

  @JsonProperty("runner")
  private Map<String, Object> runner;

  @JsonProperty("action_is_workflow")
  private Boolean actionIsWorkflow;

  @JsonProperty("notify")
  private Map<String, Object> notify;

  @JsonProperty("tags")
  private List<String> tags;

  @JsonProperty("uid")
  private String uid;

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
   * Returns the status.
   *
   * @return the status value
   * @since 0.1.0
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status.
   *
   * @param status the status to set
   * @since 0.1.0
   */
  public void setStatus(String status) {
    this.status = status;
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
   * Returns the result.
   *
   * @return the result value
   * @since 0.1.0
   */
  public Object getResult() {
    return result;
  }

  /**
   * Sets the result.
   *
   * @param result the result to set
   * @since 0.1.0
   */
  public void setResult(Object result) {
    this.result = result;
  }

  /**
   * Returns the start timestamp.
   *
   * @return the start timestamp value
   * @since 0.1.0
   */
  public String getStartTimestamp() {
    return startTimestamp;
  }

  /**
   * Sets the start timestamp.
   *
   * @param startTimestamp the start timestamp to set
   * @since 0.1.0
   */
  public void setStartTimestamp(String startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  /**
   * Returns the end timestamp.
   *
   * @return the end timestamp value
   * @since 0.1.0
   */
  public String getEndTimestamp() {
    return endTimestamp;
  }

  /**
   * Sets the end timestamp.
   *
   * @param endTimestamp the end timestamp to set
   * @since 0.1.0
   */
  public void setEndTimestamp(String endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  /**
   * Returns the delay.
   *
   * @return the delay value
   * @since 0.1.0
   */
  public Integer getDelay() {
    return delay;
  }

  /**
   * Sets the delay.
   *
   * @param delay the delay to set
   * @since 0.1.0
   */
  public void setDelay(Integer delay) {
    this.delay = delay;
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
   * Returns the liveaction.
   *
   * @return the liveaction value
   * @since 0.1.0
   */
  public Map<String, Object> getLiveaction() {
    return liveaction;
  }

  /**
   * Sets the liveaction.
   *
   * @param liveaction the liveaction to set
   * @since 0.1.0
   */
  public void setLiveaction(Map<String, Object> liveaction) {
    this.liveaction = liveaction;
  }

  /**
   * Returns the children.
   *
   * @return the children value
   * @since 0.1.0
   */
  public List<String> getChildren() {
    return children;
  }

  /**
   * Sets the children.
   *
   * @param children the children to set
   * @since 0.1.0
   */
  public void setChildren(List<String> children) {
    this.children = children;
  }

  /**
   * Returns the parent.
   *
   * @return the parent value
   * @since 0.1.0
   */
  public String getParent() {
    return parent;
  }

  /**
   * Sets the parent.
   *
   * @param parent the parent to set
   * @since 0.1.0
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Returns the depth.
   *
   * @return the depth value
   * @since 0.1.0
   */
  public Integer getDepth() {
    return depth;
  }

  /**
   * Sets the depth.
   *
   * @param depth the depth to set
   * @since 0.1.0
   */
  public void setDepth(Integer depth) {
    this.depth = depth;
  }

  /**
   * Returns the task.
   *
   * @return the task value
   * @since 0.1.0
   */
  public String getTask() {
    return task;
  }

  /**
   * Sets the task.
   *
   * @param task the task to set
   * @since 0.1.0
   */
  public void setTask(String task) {
    this.task = task;
  }

  /**
   * Returns the runner.
   *
   * @return the runner value
   * @since 0.1.0
   */
  public Map<String, Object> getRunner() {
    return runner;
  }

  /**
   * Sets the runner.
   *
   * @param runner the runner to set
   * @since 0.1.0
   */
  public void setRunner(Map<String, Object> runner) {
    this.runner = runner;
  }

  /**
   * Returns the action is workflow.
   *
   * @return the action is workflow value
   * @since 0.1.0
   */
  public Boolean getActionIsWorkflow() {
    return actionIsWorkflow;
  }

  /**
   * Sets the action is workflow.
   *
   * @param actionIsWorkflow the action is workflow to set
   * @since 0.1.0
   */
  public void setActionIsWorkflow(Boolean actionIsWorkflow) {
    this.actionIsWorkflow = actionIsWorkflow;
  }

  /**
   * Returns the notify.
   *
   * @return the notify value
   * @since 0.1.0
   */
  public Map<String, Object> getNotify() {
    return notify;
  }

  /**
   * Sets the notify.
   *
   * @param notify the notify to set
   * @since 0.1.0
   */
  public void setNotify(Map<String, Object> notify) {
    this.notify = notify;
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
    Execution other = (Execution) o;
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
   * Builder for creating Execution instances.
   *
   * @since 0.1.0
   */
  public static class Builder {
    private final String id;
    private Map<String, Object> action;
    private String status;
    private Map<String, Object> parameters;
    private Object result;
    private String startTimestamp;
    private String endTimestamp;
    private Integer delay;
    private String user;
    private Map<String, Object> context;
    private Map<String, Object> liveaction;
    private List<String> children;
    private String parent;
    private Integer depth;
    private String task;
    private Map<String, Object> runner;
    private Boolean actionIsWorkflow;
    private Map<String, Object> notify;
    private List<String> tags;
    private String uid;

    /**
     * Constructs a new Builder with the required fields.
     *
     * @param id the id
     * @since 0.1.0
     */
    public Builder(String id) {
      this.id = java.util.Objects.requireNonNull(id, "id must not be null");
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
     * Sets the status.
     *
     * @param status the status to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder status(String status) {
      this.status = status;
      return this;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the parameters to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder parameters(Map<String, Object> parameters) {
      this.parameters = parameters;
      return this;
    }

    /**
     * Sets the result.
     *
     * @param result the result to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder result(Object result) {
      this.result = result;
      return this;
    }

    /**
     * Sets the start timestamp.
     *
     * @param startTimestamp the start timestamp to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder startTimestamp(String startTimestamp) {
      this.startTimestamp = startTimestamp;
      return this;
    }

    /**
     * Sets the end timestamp.
     *
     * @param endTimestamp the end timestamp to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder endTimestamp(String endTimestamp) {
      this.endTimestamp = endTimestamp;
      return this;
    }

    /**
     * Sets the delay.
     *
     * @param delay the delay to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder delay(Integer delay) {
      this.delay = delay;
      return this;
    }

    /**
     * Sets the user.
     *
     * @param user the user to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder user(String user) {
      this.user = user;
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
     * Sets the liveaction.
     *
     * @param liveaction the liveaction to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder liveaction(Map<String, Object> liveaction) {
      this.liveaction = liveaction;
      return this;
    }

    /**
     * Sets the children.
     *
     * @param children the children to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder children(List<String> children) {
      this.children = children;
      return this;
    }

    /**
     * Sets the parent.
     *
     * @param parent the parent to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder parent(String parent) {
      this.parent = parent;
      return this;
    }

    /**
     * Sets the depth.
     *
     * @param depth the depth to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder depth(Integer depth) {
      this.depth = depth;
      return this;
    }

    /**
     * Sets the task.
     *
     * @param task the task to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder task(String task) {
      this.task = task;
      return this;
    }

    /**
     * Sets the runner.
     *
     * @param runner the runner to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder runner(Map<String, Object> runner) {
      this.runner = runner;
      return this;
    }

    /**
     * Sets the action is workflow.
     *
     * @param actionIsWorkflow the action is workflow to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder actionIsWorkflow(Boolean actionIsWorkflow) {
      this.actionIsWorkflow = actionIsWorkflow;
      return this;
    }

    /**
     * Sets the notify.
     *
     * @param notify the notify to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder notify(Map<String, Object> notify) {
      this.notify = notify;
      return this;
    }

    /**
     * Sets the tags.
     *
     * @param tags the tags to set
     * @return this builder instance
     * @since 0.1.0
     */
    public Builder tags(List<String> tags) {
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
     * Builds the resource instance with all configured properties.
     *
     * @return the constructed resource instance
     * @since 0.1.0
     */
    public Execution build() {
      Execution exec = new Execution();
      exec.setId(id);
      exec.setAction(action);
      exec.setStatus(status);
      exec.setParameters(parameters);
      exec.setResult(result);
      exec.setStartTimestamp(startTimestamp);
      exec.setEndTimestamp(endTimestamp);
      exec.setDelay(delay);
      exec.setUser(user);
      exec.setContext(context);
      exec.setLiveaction(liveaction);
      exec.setChildren(children);
      exec.setParent(parent);
      exec.setDepth(depth);
      exec.setTask(task);
      exec.setRunner(runner);
      exec.setActionIsWorkflow(actionIsWorkflow);
      exec.setNotify(notify);
      exec.setTags(tags);
      exec.setUid(uid);
      return exec;
    }
  }
}
