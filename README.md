# st2client-j

StackStorm Java SDK. Use `St2Client` to call the StackStorm REST API.

```java
St2Client client = St2Client.builder()
        .baseUrl("http://stackstorm.example.com")
        .username("admin")
        .password(System.getenv("ST2_PASSWORD"))
        .build();

List<Action> actions = client.actions().getAll();
Execution exec = client.executions().execute("core.local", Map.of("cmd", "echo hello"));
client.close();
```

Auth: username/password, static token, or API key. Password auth re-fetches a token on HTTP 401.

For Spring Boot, add `st2client-spring-boot-starter` and inject `St2Client` after setting `st2.base-url`:

```yaml
st2:
  base-url: http://stackstorm.example.com
  auth:
    username: admin
    password: ${ST2_PASSWORD}
```

## Resources

| Accessor | Description |
|--------|------|
| `actions()` | Action: `getEntryPoint`, `clone`, `deleteAction` |
| `executions()` | Execution: run, pause, rerun, output, wait for completion, `inspect` |
| `rules()` / `triggers()` | Rule / Trigger |
| `packs()` | Pack: install, uninstall, search, register |
| `keys()` | KeyValue, addressed by name |
| `tokens()` / `apiKeys()` | Token / API Key |
| `sensors()` / `policies()` | Sensor / Policy |
| `actionAliases()` | `match()` |
| `inquiries()` | `respond()` |
| `triggerInstances()` | `reemit()` |
| `webhooks()` | Webhook |
| `configs()` | Update by pack name |
| `triggerTypes()` / `policyTypes()` / `ruleEnforcements()` / `configSchemas()` / `timers()` / `traces()` / `runnerTypes()` | Read-only |
| `streamClient()` | SSE |

Apache License 2.0.
