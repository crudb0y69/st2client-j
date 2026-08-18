package io.github.st2client.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.st2client.exception.ConfigurationException;
import io.github.st2client.exception.OperationFailureException;
import io.github.st2client.exception.St2ClientException;
import io.github.st2client.resource.ResourceDescriptor;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Round-trip serialization coverage tests for all model classes. */
class ModelCoverageTest {

  @Test
  void shouldRoundTripAction() {
    Action a = new Action();
    a.setId("1");
    a.setName("test");
    a.setPack("p");
    a.setEnabled(true);
    a.setRunnerType("python");
    a.setEntryPoint("main.py");

    Map<String, Object> map = a.toMap();
    Action restored = Resource.fromMap(map, Action.class);
    assertThat(restored.getName()).isEqualTo("test");
    assertThat(restored.getPack()).isEqualTo("p");
    assertThat(restored.getEnabled()).isTrue();
  }

  @Test
  void shouldRoundTripExecution() {
    Execution e = new Execution();
    e.setId("e1");
    e.setStatus("running");
    e.setDelay(5);

    Map<String, Object> map = e.toMap();
    Execution restored = Resource.fromMap(map, Execution.class);
    assertThat(restored.getStatus()).isEqualTo("running");
    assertThat(restored.getDelay()).isEqualTo(5);
  }

  @Test
  void shouldRoundTripRule() {
    Rule r = new Rule();
    r.setId("r1");
    r.setName("my-rule");
    r.setPack("p");
    r.setEnabled(true);
    r.setTrigger(Map.of("type", "timer"));

    Map<String, Object> map = r.toMap();
    Rule restored = Resource.fromMap(map, Rule.class);
    assertThat(restored.getName()).isEqualTo("my-rule");
    assertThat(restored.getEnabled()).isTrue();
  }

  @Test
  void shouldRoundTripTrigger() {
    Trigger t = new Trigger();
    t.setId("t1");
    t.setType("timer");
    t.setRef("p.my-trigger");

    Map<String, Object> map = t.toMap();
    Trigger restored = Resource.fromMap(map, Trigger.class);
    assertThat(restored.getType()).isEqualTo("timer");
    assertThat(restored.getRef()).isEqualTo("p.my-trigger");
  }

  @Test
  void shouldRoundTripPack() {
    Pack p = new Pack();
    p.setId("p1");
    p.setName("nginx");
    p.setVersion("1.0.0");

    Map<String, Object> map = p.toMap();
    Pack restored = Resource.fromMap(map, Pack.class);
    assertThat(restored.getName()).isEqualTo("nginx");
    assertThat(restored.getVersion()).isEqualTo("1.0.0");
  }

  @Test
  void shouldDeserializePackSystemAsObject() {
    Pack pack =
        Resource.fromMap(
            Resource.readJson("{\"name\":\"chatops\",\"system\":{}}", Resource.MAP_TYPE),
            Pack.class);
    assertThat(pack.getName()).isEqualTo("chatops");
    assertThat(pack.getSystem()).isEmpty();
  }

  @Test
  void shouldRoundTripApiKey() {
    ApiKey k = new ApiKey();
    k.setId("k1");
    k.setUid("uid-1");
    k.setUser("admin");
    k.setKeyHash("abc123");
    k.setEnabled(true);
    k.setCreatedAt("2025-01-01T00:00:00Z");
    k.setMetadata(Map.of("used_by", "test"));

    Map<String, Object> map = k.toMap();
    ApiKey restored = Resource.fromMap(map, ApiKey.class);
    assertThat(restored.getId()).isEqualTo("k1");
    assertThat(restored.getUid()).isEqualTo("uid-1");
    assertThat(restored.getUser()).isEqualTo("admin");
    assertThat(restored.getKeyHash()).isEqualTo("abc123");
    assertThat(restored.getEnabled()).isTrue();
    assertThat(restored.getCreatedAt()).isEqualTo("2025-01-01T00:00:00Z");
    assertThat(restored.getMetadata()).containsEntry("used_by", "test");
  }

  @Test
  void shouldRoundTripToken() {
    Token t = new Token();
    t.setId("t1");
    t.setToken("secret");
    t.setExpiry("2099-01-01T00:00:00Z");

    Map<String, Object> map = t.toMap();
    Token restored = Resource.fromMap(map, Token.class);
    assertThat(restored.getToken()).isEqualTo("secret");
  }

  @Test
  void shouldRoundTripKeyValuePair() {
    KeyValuePair kv = new KeyValuePair();
    kv.setName("mykey");
    kv.setValue("myval");
    kv.setSecret(true);

    Map<String, Object> map = kv.toMap();
    KeyValuePair restored = Resource.fromMap(map, KeyValuePair.class);
    assertThat(restored.getValue()).isEqualTo("myval");
    assertThat(restored.getSecret()).isTrue();
  }

  @Test
  void shouldRoundTripSensor() {
    Sensor s = new Sensor();
    s.setId("s1");
    s.setName("my-sensor");
    s.setPack("p");
    s.setEnabled(true);
    s.setClassName("MySensor");
    s.setPollInterval(10.0);

    Map<String, Object> map = s.toMap();
    Sensor restored = Resource.fromMap(map, Sensor.class);
    assertThat(restored.getName()).isEqualTo("my-sensor");
    assertThat(restored.getPack()).isEqualTo("p");
    assertThat(restored.getEnabled()).isTrue();
    assertThat(restored.getClassName()).isEqualTo("MySensor");
    assertThat(restored.getPollInterval()).isEqualTo(10.0);
  }

  @Test
  void shouldRoundTripInquiry() {
    Inquiry i = new Inquiry();
    i.setId("i1");
    i.setRoute("slack");
    i.setTtl(300);
    i.setStatus("pending");
    i.setUsers(List.of("admin"));

    Map<String, Object> map = i.toMap();
    Inquiry restored = Resource.fromMap(map, Inquiry.class);
    assertThat(restored.getRoute()).isEqualTo("slack");
    assertThat(restored.getTtl()).isEqualTo(300);
    assertThat(restored.getStatus()).isEqualTo("pending");
    assertThat(restored.getUsers()).containsExactly("admin");
  }

  @Test
  void shouldRoundTripActionAlias() {
    ActionAlias aa = new ActionAlias();
    aa.setId("aa1");
    aa.setName("my-alias");
    aa.setPack("p");
    aa.setActionRef("core.local");
    aa.setEnabled(true);

    Map<String, Object> map = aa.toMap();
    ActionAlias restored = Resource.fromMap(map, ActionAlias.class);
    assertThat(restored.getName()).isEqualTo("my-alias");
    assertThat(restored.getActionRef()).isEqualTo("core.local");
    assertThat(restored.getEnabled()).isTrue();
  }

  @Test
  void shouldRoundTripTriggerType() {
    TriggerType tt = new TriggerType();
    tt.setId("tt1");
    tt.setName("my-trigger-type");
    tt.setPack("p");
    tt.setRef("p.my-trigger-type");
    tt.setDescription("test");

    Map<String, Object> map = tt.toMap();
    TriggerType restored = Resource.fromMap(map, TriggerType.class);
    assertThat(restored.getName()).isEqualTo("my-trigger-type");
    assertThat(restored.getRef()).isEqualTo("p.my-trigger-type");
    assertThat(restored.getDescription()).isEqualTo("test");
  }

  @Test
  void shouldRoundTripTriggerInstance() {
    TriggerInstance ti = new TriggerInstance();
    ti.setId("ti1");
    ti.setTrigger("timer");
    ti.setStatus("complete");

    Map<String, Object> map = ti.toMap();
    TriggerInstance restored = Resource.fromMap(map, TriggerInstance.class);
    assertThat(restored.getTrigger()).isEqualTo("timer");
    assertThat(restored.getStatus()).isEqualTo("complete");
  }

  @Test
  void shouldRoundTripPolicyType() {
    PolicyType pt = new PolicyType();
    pt.setId("pt1");
    pt.setName("action");
    pt.setResourceType("action");
    pt.setRef("action");
    pt.setEnabled(true);

    Map<String, Object> map = pt.toMap();
    PolicyType restored = Resource.fromMap(map, PolicyType.class);
    assertThat(restored.getName()).isEqualTo("action");
    assertThat(restored.getResourceType()).isEqualTo("action");
    assertThat(restored.getEnabled()).isTrue();
  }

  @Test
  void shouldRoundTripPolicy() {
    Policy p = new Policy();
    p.setId("p1");
    p.setName("my-policy");
    p.setPack("pack");
    p.setPolicyType("action");
    p.setResourceRef("core.local");

    Map<String, Object> map = p.toMap();
    Policy restored = Resource.fromMap(map, Policy.class);
    assertThat(restored.getName()).isEqualTo("my-policy");
    assertThat(restored.getPolicyType()).isEqualTo("action");
    assertThat(restored.getResourceRef()).isEqualTo("core.local");
  }

  @Test
  void shouldRoundTripRuleEnforcement() {
    RuleEnforcement re = new RuleEnforcement();
    re.setId("re1");
    re.setTriggerInstanceId("ti1");
    re.setExecutionId("e1");
    re.setEnforcedAt("2025-01-01T00:00:00Z");

    Map<String, Object> map = re.toMap();
    RuleEnforcement restored = Resource.fromMap(map, RuleEnforcement.class);
    assertThat(restored.getTriggerInstanceId()).isEqualTo("ti1");
    assertThat(restored.getExecutionId()).isEqualTo("e1");
    assertThat(restored.getEnforcedAt()).isEqualTo("2025-01-01T00:00:00Z");
  }

  @Test
  void shouldCoverExceptions() {
    assertThat(new St2ClientException("base").getMessage()).isEqualTo("base");
    assertThat(new ConfigurationException("cfg").getMessage()).isEqualTo("cfg");
    assertThat(new OperationFailureException(500, "boom").getStatusCode()).isEqualTo(500);
  }

  @Test
  void shouldRoundTripWebhook() {
    Webhook w = new Webhook();
    w.setId("w1");
    w.setRef("my-pack.my-hook");
    w.setUid("uid-w1");
    w.setName("my-hook");
    w.setPack("my-pack");
    w.setDescription("test webhook");
    w.setType("standard");
    w.setParameters(Map.of("key", "val"));
    w.setMetadataFile("meta.yaml");

    Map<String, Object> map = w.toMap();
    Webhook restored = Resource.fromMap(map, Webhook.class);
    assertThat(restored.getName()).isEqualTo("my-hook");
    assertThat(restored.getPack()).isEqualTo("my-pack");
    assertThat(restored.getType()).isEqualTo("standard");
    assertThat(restored.getRef()).isEqualTo("my-pack.my-hook");
    assertThat(restored.getParameters()).containsEntry("key", "val");
    assertThat(restored.getMetadataFile()).isEqualTo("meta.yaml");
  }

  @Test
  void shouldRoundTripConfig() {
    Config c = new Config();
    c.setId("c1");
    c.setPack("my-pack");
    c.setValues(Map.of("setting1", "value1", "setting2", 42));

    Map<String, Object> map = c.toMap();
    Config restored = Resource.fromMap(map, Config.class);
    assertThat(restored.getPack()).isEqualTo("my-pack");
    assertThat(restored.getValues()).containsEntry("setting1", "value1");
  }

  @Test
  void shouldRoundTripConfigSchema() {
    ConfigSchema cs = new ConfigSchema();
    cs.setId("cs1");
    cs.setPack("my-pack");
    cs.setAttributes(Map.of("attr1", Map.of("type", "string")));

    Map<String, Object> map = cs.toMap();
    ConfigSchema restored = Resource.fromMap(map, ConfigSchema.class);
    assertThat(restored.getPack()).isEqualTo("my-pack");
    assertThat(restored.getAttributes()).containsKey("attr1");
  }

  @Test
  void shouldRoundTripTrace() {
    Trace t = new Trace();
    t.setId("tr1");
    t.setUid("uid-tr1");
    t.setTraceTag("tag-1");
    t.setStartTimestamp("2025-01-01T00:00:00Z");
    t.setEndTimestamp("2025-01-01T01:00:00Z");
    t.setActionExecutions(List.of(Map.of("id", "e1")));
    t.setTriggerInstances(List.of(Map.of("id", "ti1")));
    t.setRuleEnforcements(List.of(Map.of("id", "re1")));

    Map<String, Object> map = t.toMap();
    Trace restored = Resource.fromMap(map, Trace.class);
    assertThat(restored.getTraceTag()).isEqualTo("tag-1");
    assertThat(restored.getStartTimestamp()).isEqualTo("2025-01-01T00:00:00Z");
    assertThat(restored.getEndTimestamp()).isEqualTo("2025-01-01T01:00:00Z");
    assertThat(restored.getActionExecutions()).hasSize(1);
    assertThat(restored.getTriggerInstances()).hasSize(1);
    assertThat(restored.getRuleEnforcements()).hasSize(1);
    assertThat(restored.getUid()).isEqualTo("uid-tr1");
  }

  @Test
  void shouldRoundTripRunnerType() {
    RunnerType rt = new RunnerType();
    rt.setId("rt1");
    rt.setUid("uid-rt1");
    rt.setName("python");
    rt.setEnabled(true);
    rt.setDescription("Python runner");
    rt.setRunnerModule("python_runner");
    rt.setRunnerParameters(Map.of("timeout", 60));
    rt.setQueryModule("query_module");

    Map<String, Object> map = rt.toMap();
    RunnerType restored = Resource.fromMap(map, RunnerType.class);
    assertThat(restored.getName()).isEqualTo("python");
    assertThat(restored.getEnabled()).isTrue();
    assertThat(restored.getRunnerModule()).isEqualTo("python_runner");
    assertThat(restored.getDescription()).isEqualTo("Python runner");
    assertThat(restored.getRunnerParameters()).containsEntry("timeout", 60);
    assertThat(restored.getQueryModule()).isEqualTo("query_module");
    assertThat(restored.getUid()).isEqualTo("uid-rt1");
  }

  @Test
  void shouldRoundTripTimer() {
    Timer t = new Timer();
    t.setId("t1");
    t.setRef("my-pack.my-timer");
    t.setUid("uid-t1");
    t.setName("my-timer");
    t.setPack("my-pack");
    t.setDescription("test timer");
    t.setType("interval");
    t.setParameters(Map.of("delta", 30));

    Map<String, Object> map = t.toMap();
    Timer restored = Resource.fromMap(map, Timer.class);
    assertThat(restored.getName()).isEqualTo("my-timer");
    assertThat(restored.getPack()).isEqualTo("my-pack");
    assertThat(restored.getType()).isEqualTo("interval");
    assertThat(restored.getParameters()).containsEntry("delta", 30);
  }

  @Test
  void shouldTestActionEqualsAndHashCode() {
    Action a1 = new Action();
    a1.setId("1");
    a1.setName("test");
    Action a2 = new Action();
    a2.setId("1");
    a2.setName("other");
    Action a3 = new Action();
    a3.setId("2");
    a3.setName("test");

    assertThat(a1).isEqualTo(a2);
    assertThat(a1).isNotEqualTo(a3);
    assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    assertThat(a1.hashCode()).isNotEqualTo(a3.hashCode());
  }

  @Test
  void shouldTestExecutionEqualsAndHashCode() {
    Execution e1 = new Execution();
    e1.setId("1");
    e1.setStatus("running");
    Execution e2 = new Execution();
    e2.setId("1");
    e2.setStatus("succeeded");
    Execution e3 = new Execution();
    e3.setId("2");
    e3.setStatus("running");

    assertThat(e1).isEqualTo(e2);
    assertThat(e1).isNotEqualTo(e3);
    assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
  }

  @Test
  void shouldTestRuleEqualsAndHashCode() {
    Rule r1 = new Rule();
    r1.setId("1");
    r1.setName("rule1");
    Rule r2 = new Rule();
    r2.setId("1");
    r2.setName("rule2");
    Rule r3 = new Rule();
    r3.setId("2");
    r3.setName("rule1");

    assertThat(r1).isEqualTo(r2);
    assertThat(r1).isNotEqualTo(r3);
    assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
  }

  @Test
  void shouldTestKeyValuePairEqualsAndHashCode() {
    KeyValuePair kv1 = new KeyValuePair();
    kv1.setName("key1");
    kv1.setValue("val1");
    KeyValuePair kv2 = new KeyValuePair();
    kv2.setName("key1");
    kv2.setValue("val2");
    KeyValuePair kv3 = new KeyValuePair();
    kv3.setName("key2");
    kv3.setValue("val1");

    assertThat(kv1).isEqualTo(kv2);
    assertThat(kv1).isNotEqualTo(kv3);
    assertThat(kv1.hashCode()).isEqualTo(kv2.hashCode());
  }

  @Test
  void shouldTestResourceDescriptorEqualsAndHashCode() {
    ResourceDescriptor<?> rd1 = ResourceDescriptor.ACTION;
    ResourceDescriptor<?> rd2 = new ResourceDescriptor<>(Action.class, "actions");
    ResourceDescriptor<?> rd3 = ResourceDescriptor.EXECUTION;

    assertThat(rd1).isEqualTo(rd2);
    assertThat(rd1).isNotEqualTo(rd3);
    assertThat(rd1.hashCode()).isEqualTo(rd2.hashCode());
  }

  @Test
  void shouldBuildActionWithBuilder() {
    Action a =
        new Action.Builder("my-action", "my-pack")
            .id("1")
            .enabled(true)
            .runnerType("python")
            .entryPoint("main.py")
            .description("test action")
            .build();

    assertThat(a.getId()).isEqualTo("1");
    assertThat(a.getName()).isEqualTo("my-action");
    assertThat(a.getPack()).isEqualTo("my-pack");
    assertThat(a.getEnabled()).isTrue();
    assertThat(a.getRunnerType()).isEqualTo("python");
    assertThat(a.getEntryPoint()).isEqualTo("main.py");
    assertThat(a.getDescription()).isEqualTo("test action");
  }

  @Test
  void shouldBuildExecutionWithBuilder() {
    Execution e = new Execution.Builder("exec-1").status("running").delay(5).user("admin").build();

    assertThat(e.getId()).isEqualTo("exec-1");
    assertThat(e.getStatus()).isEqualTo("running");
    assertThat(e.getDelay()).isEqualTo(5);
    assertThat(e.getUser()).isEqualTo("admin");
  }

  @Test
  void shouldBuildRuleWithBuilder() {
    Rule r =
        new Rule.Builder("my-rule", "my-pack")
            .id("1")
            .enabled(true)
            .trigger(Map.of("type", "timer"))
            .description("test rule")
            .build();

    assertThat(r.getId()).isEqualTo("1");
    assertThat(r.getName()).isEqualTo("my-rule");
    assertThat(r.getPack()).isEqualTo("my-pack");
    assertThat(r.getEnabled()).isTrue();
    assertThat(r.getTrigger()).containsEntry("type", "timer");
    assertThat(r.getDescription()).isEqualTo("test rule");
  }

  @Test
  void shouldTestTraceEqualsAndHashCode() {
    Trace t1 = new Trace();
    t1.setId("1");
    t1.setTraceTag("tag-1");
    Trace t2 = new Trace();
    t2.setId("1");
    t2.setTraceTag("tag-2");
    Trace t3 = new Trace();
    t3.setId("2");
    t3.setTraceTag("tag-1");

    assertThat(t1).isEqualTo(t2);
    assertThat(t1).isNotEqualTo(t3);
    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    assertThat(t1.hashCode()).isNotEqualTo(t3.hashCode());
  }

  @Test
  void shouldTestRunnerTypeEqualsAndHashCode() {
    RunnerType r1 = new RunnerType();
    r1.setId("1");
    r1.setName("python");
    RunnerType r2 = new RunnerType();
    r2.setId("1");
    r2.setName("mistral");
    RunnerType r3 = new RunnerType();
    r3.setId("2");
    r3.setName("python");

    assertThat(r1).isEqualTo(r2);
    assertThat(r1).isNotEqualTo(r3);
    assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    assertThat(r1.hashCode()).isNotEqualTo(r3.hashCode());
  }

  @Test
  void shouldTestAllModelEqualsAndHashCode() {
    Trigger t1 = new Trigger();
    t1.setId("1");
    Trigger t2 = new Trigger();
    t2.setId("1");
    assertThat(t1).isEqualTo(t2);

    TriggerType tt1 = new TriggerType();
    tt1.setId("1");
    TriggerType tt2 = new TriggerType();
    tt2.setId("1");
    assertThat(tt1).isEqualTo(tt2);

    TriggerInstance ti1 = new TriggerInstance();
    ti1.setId("1");
    TriggerInstance ti2 = new TriggerInstance();
    ti2.setId("1");
    assertThat(ti1).isEqualTo(ti2);

    Pack p1 = new Pack();
    p1.setId("1");
    Pack p2 = new Pack();
    p2.setId("1");
    assertThat(p1).isEqualTo(p2);

    Token tk1 = new Token();
    tk1.setId("1");
    Token tk2 = new Token();
    tk2.setId("1");
    assertThat(tk1).isEqualTo(tk2);

    ApiKey ak1 = new ApiKey();
    ak1.setId("1");
    ApiKey ak2 = new ApiKey();
    ak2.setId("1");
    assertThat(ak1).isEqualTo(ak2);

    Sensor s1 = new Sensor();
    s1.setId("1");
    Sensor s2 = new Sensor();
    s2.setId("1");
    assertThat(s1).isEqualTo(s2);

    Inquiry i1 = new Inquiry();
    i1.setId("1");
    Inquiry i2 = new Inquiry();
    i2.setId("1");
    assertThat(i1).isEqualTo(i2);

    ActionAlias aa1 = new ActionAlias();
    aa1.setId("1");
    ActionAlias aa2 = new ActionAlias();
    aa2.setId("1");
    assertThat(aa1).isEqualTo(aa2);

    Policy pol1 = new Policy();
    pol1.setId("1");
    Policy pol2 = new Policy();
    pol2.setId("1");
    assertThat(pol1).isEqualTo(pol2);

    PolicyType pt1 = new PolicyType();
    pt1.setId("1");
    PolicyType pt2 = new PolicyType();
    pt2.setId("1");
    assertThat(pt1).isEqualTo(pt2);

    RuleEnforcement re1 = new RuleEnforcement();
    re1.setId("1");
    RuleEnforcement re2 = new RuleEnforcement();
    re2.setId("1");
    assertThat(re1).isEqualTo(re2);

    Webhook w1 = new Webhook();
    w1.setId("1");
    Webhook w2 = new Webhook();
    w2.setId("1");
    assertThat(w1).isEqualTo(w2);

    Config c1 = new Config();
    c1.setId("1");
    Config c2 = new Config();
    c2.setId("1");
    assertThat(c1).isEqualTo(c2);

    ConfigSchema cs1 = new ConfigSchema();
    cs1.setId("1");
    ConfigSchema cs2 = new ConfigSchema();
    cs2.setId("1");
    assertThat(cs1).isEqualTo(cs2);

    Timer tm1 = new Timer();
    tm1.setId("1");
    Timer tm2 = new Timer();
    tm2.setId("1");
    assertThat(tm1).isEqualTo(tm2);

    UserInfo u1 = new UserInfo();
    u1.setId("1");
    UserInfo u2 = new UserInfo();
    u2.setId("1");
    assertThat(u1).isEqualTo(u2);

    Trace tr1 = new Trace();
    tr1.setId("1");
    Trace tr2 = new Trace();
    tr2.setId("1");
    assertThat(tr1).isEqualTo(tr2);

    Trace tr3 = new Trace();
    tr3.setId("2");
    assertThat(tr1).isNotEqualTo(tr3);

    RunnerType rt1 = new RunnerType();
    rt1.setId("1");
    RunnerType rt2 = new RunnerType();
    rt2.setId("1");
    assertThat(rt1).isEqualTo(rt2);

    RunnerType rt3 = new RunnerType();
    rt3.setId("2");
    assertThat(rt1).isNotEqualTo(rt3);
  }

  @Test
  void mapperCopyMustNotAffectSharedDeserialization() {
    ObjectMapper copy = Resource.mapper();
    copy.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    Action action =
        Resource.fromJson("{\"id\":\"1\",\"name\":\"local\",\"unknown_field\":true}", Action.class);
    assertThat(action.getId()).isEqualTo("1");
    assertThat(action.getName()).isEqualTo("local");
  }
}
