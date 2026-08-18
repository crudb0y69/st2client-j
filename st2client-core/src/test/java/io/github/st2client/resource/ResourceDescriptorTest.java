package io.github.st2client.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.model.Action;
import io.github.st2client.model.Execution;

import org.junit.jupiter.api.Test;

/** Tests for {@link ResourceDescriptor} routing metadata. */
class ResourceDescriptorTest {

  @Test
  void shouldExposeResourceClassAndUrlPath() {
    ResourceDescriptor<Action> descriptor = new ResourceDescriptor<>(Action.class, "actions");
    assertThat(descriptor.resourceClass()).isEqualTo(Action.class);
    assertThat(descriptor.urlPath()).isEqualTo("actions");
    assertThat(descriptor.typeName()).isEqualTo("Action");
  }

  @Test
  void typeNameShouldComeFromClassNotCliAlias() {
    assertThat(ResourceDescriptor.ACTION_ALIAS.typeName()).isEqualTo("ActionAlias");
    assertThat(ResourceDescriptor.POLICY_TYPE.typeName()).isEqualTo("PolicyType");
    assertThat(ResourceDescriptor.RULE_ENFORCEMENT.typeName()).isEqualTo("RuleEnforcement");
    assertThat(ResourceDescriptor.TRIGGER_INSTANCE.typeName()).isEqualTo("TriggerInstance");
    assertThat(ResourceDescriptor.SENSOR.typeName()).isEqualTo("Sensor");
  }

  @Test
  void equalsShouldMatchOnUrlPath() {
    ResourceDescriptor<Action> samePath = new ResourceDescriptor<>(Action.class, "actions");
    assertThat(ResourceDescriptor.ACTION).isEqualTo(samePath);
    assertThat(ResourceDescriptor.ACTION).isNotEqualTo(ResourceDescriptor.EXECUTION);
    assertThat(ResourceDescriptor.ACTION.hashCode()).isEqualTo(samePath.hashCode());
  }

  @Test
  void constantsShouldMapToApiPaths() {
    assertThat(ResourceDescriptor.EXECUTION.resourceClass()).isEqualTo(Execution.class);
    assertThat(ResourceDescriptor.EXECUTION.urlPath()).isEqualTo("executions");
    assertThat(ResourceDescriptor.KEY_VALUE_PAIR.urlPath()).isEqualTo("keys");
    assertThat(ResourceDescriptor.API_KEY.urlPath()).isEqualTo("api_keys");
  }
}
