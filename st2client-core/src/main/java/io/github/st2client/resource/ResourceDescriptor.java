package io.github.st2client.resource;

import io.github.st2client.model.Action;
import io.github.st2client.model.ActionAlias;
import io.github.st2client.model.ApiKey;
import io.github.st2client.model.Config;
import io.github.st2client.model.ConfigSchema;
import io.github.st2client.model.Execution;
import io.github.st2client.model.Inquiry;
import io.github.st2client.model.KeyValuePair;
import io.github.st2client.model.Pack;
import io.github.st2client.model.Policy;
import io.github.st2client.model.PolicyType;
import io.github.st2client.model.Resource;
import io.github.st2client.model.Rule;
import io.github.st2client.model.RuleEnforcement;
import io.github.st2client.model.RunnerType;
import io.github.st2client.model.Sensor;
import io.github.st2client.model.Timer;
import io.github.st2client.model.Token;
import io.github.st2client.model.Trace;
import io.github.st2client.model.Trigger;
import io.github.st2client.model.TriggerInstance;
import io.github.st2client.model.TriggerType;
import io.github.st2client.model.Webhook;

/**
 * Routing metadata for a StackStorm resource type: the model class and API path segment.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class ResourceDescriptor<T extends Resource> {

  private final Class<T> resourceClass;
  private final String urlPath;

  public ResourceDescriptor(Class<T> resourceClass, String urlPath) {
    this.resourceClass = resourceClass;
    this.urlPath = urlPath;
  }

  /**
   * Returns the resource class type.
   *
   * @return the resource class
   * @since 0.1.0
   */
  public Class<T> resourceClass() {
    return resourceClass;
  }

  /**
   * Returns the URL path segment for this resource type.
   *
   * @return the URL path
   * @since 0.1.0
   */
  public String urlPath() {
    return urlPath;
  }

  /**
   * Returns the model simple class name for error messages.
   *
   * @return the type name
   * @since 0.1.0
   */
  public String typeName() {
    return resourceClass.getSimpleName();
  }

  public static final ResourceDescriptor<Action> ACTION =
      new ResourceDescriptor<>(Action.class, "actions");

  public static final ResourceDescriptor<Execution> EXECUTION =
      new ResourceDescriptor<>(Execution.class, "executions");

  public static final ResourceDescriptor<Rule> RULE = new ResourceDescriptor<>(Rule.class, "rules");

  public static final ResourceDescriptor<Trigger> TRIGGER =
      new ResourceDescriptor<>(Trigger.class, "triggers");

  public static final ResourceDescriptor<Pack> PACK = new ResourceDescriptor<>(Pack.class, "packs");

  public static final ResourceDescriptor<KeyValuePair> KEY_VALUE_PAIR =
      new ResourceDescriptor<>(KeyValuePair.class, "keys");

  public static final ResourceDescriptor<Token> TOKEN =
      new ResourceDescriptor<>(Token.class, "tokens");

  public static final ResourceDescriptor<ApiKey> API_KEY =
      new ResourceDescriptor<>(ApiKey.class, "api_keys");

  public static final ResourceDescriptor<TriggerType> TRIGGER_TYPE =
      new ResourceDescriptor<>(TriggerType.class, "trigger_types");

  public static final ResourceDescriptor<Sensor> SENSOR =
      new ResourceDescriptor<>(Sensor.class, "sensortypes");

  public static final ResourceDescriptor<Policy> POLICY =
      new ResourceDescriptor<>(Policy.class, "policies");

  public static final ResourceDescriptor<PolicyType> POLICY_TYPE =
      new ResourceDescriptor<>(PolicyType.class, "policytypes");

  public static final ResourceDescriptor<ActionAlias> ACTION_ALIAS =
      new ResourceDescriptor<>(ActionAlias.class, "actionalias");

  public static final ResourceDescriptor<Inquiry> INQUIRY =
      new ResourceDescriptor<>(Inquiry.class, "inquiries");

  public static final ResourceDescriptor<RuleEnforcement> RULE_ENFORCEMENT =
      new ResourceDescriptor<>(RuleEnforcement.class, "ruleenforcements");

  public static final ResourceDescriptor<TriggerInstance> TRIGGER_INSTANCE =
      new ResourceDescriptor<>(TriggerInstance.class, "triggerinstances");

  public static final ResourceDescriptor<Webhook> WEBHOOK =
      new ResourceDescriptor<>(Webhook.class, "webhooks");

  public static final ResourceDescriptor<Config> CONFIG =
      new ResourceDescriptor<>(Config.class, "configs");

  public static final ResourceDescriptor<ConfigSchema> CONFIG_SCHEMA =
      new ResourceDescriptor<>(ConfigSchema.class, "config_schemas");

  public static final ResourceDescriptor<Trace> TRACE =
      new ResourceDescriptor<>(Trace.class, "traces");

  public static final ResourceDescriptor<RunnerType> RUNNER_TYPE =
      new ResourceDescriptor<>(RunnerType.class, "runnertypes");

  public static final ResourceDescriptor<Timer> TIMER =
      new ResourceDescriptor<>(Timer.class, "timers");

  public static final String PACK_INSTALL_PATH = "/packs/install";
  public static final String PACK_UNINSTALL_PATH = "/packs/uninstall";
  public static final String PACK_SEARCH_PATH = "/packs/index/search";
  public static final String PACK_REGISTER_PATH = "/packs/register";

  /**
   * Compares this instance with another object for equality based on the identifier.
   *
   * @param o the object to compare with
   * @return true if the identifiers match, false otherwise
   * @since 0.1.0
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceDescriptor<?> other = (ResourceDescriptor<?>) o;
    return urlPath != null && urlPath.equals(other.urlPath);
  }

  /**
   * Returns a hash code value for this instance.
   *
   * @return the hash code
   * @since 0.1.0
   */
  @Override
  public int hashCode() {
    return urlPath != null ? urlPath.hashCode() : 0;
  }
}
