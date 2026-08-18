package io.github.st2client.resource;

import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.ActionAlias;
import io.github.st2client.model.Resource;
import io.github.st2client.model.Webhook;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Webhooks. Provides CRUD operations as well as {@code
 * postGenericWebhook()} and {@code match()}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class WebhookClient extends ResourceClient<Webhook> {

  private static final String ST2_WEBHOOK_PATH = "st2";
  private static final String ALIAS = "alias";
  private static final String MATCH = "match";

  /**
   * Constructs a new WebhookClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public WebhookClient(St2HttpClient http) {
    super(ResourceDescriptor.WEBHOOK, http);
  }

  /**
   * Posts a generic webhook trigger
   *
   * @param trigger the trigger
   * @param payload the payload
   * @param traceTag the traceTag
   * @return the server response data
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public Map<String, Object> postGenericWebhook(
      String trigger, Map<String, Object> payload, String traceTag) throws IOException {
    Objects.requireNonNull(trigger, "trigger must not be null");
    try (Response r = http.post(st2WebhookUrl(), buildWebhookBody(trigger, payload, traceTag))) {
      return Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
    }
  }

  public CompletableFuture<Map<String, Object>> postGenericWebhookAsync(
      String trigger, Map<String, Object> payload, String traceTag) {
    Objects.requireNonNull(trigger, "trigger must not be null");
    return http.postAsync(st2WebhookUrl(), buildWebhookBody(trigger, payload, traceTag))
        .thenApply(
            r -> {
              try (r) {
                return Resource.readJson(
                    r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  public ActionAlias match(ActionAlias instance) throws IOException {
    Objects.requireNonNull(instance, "instance must not be null");
    try (Response r = http.post(aliasMatchUrl(), instance.toMap())) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          ActionAlias.class);
    }
  }

  public CompletableFuture<ActionAlias> matchAsync(ActionAlias instance) {
    Objects.requireNonNull(instance, "instance must not be null");
    return http.postAsync(aliasMatchUrl(), instance.toMap())
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    ActionAlias.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  private String st2WebhookUrl() {
    return UrlPaths.join(descriptor.urlPath(), ST2_WEBHOOK_PATH);
  }

  private String aliasMatchUrl() {
    return UrlPaths.join(descriptor.urlPath(), ST2_WEBHOOK_PATH, ALIAS, MATCH);
  }

  private Map<String, Object> buildWebhookBody(
      String trigger, Map<String, Object> payload, String traceTag) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("trigger", trigger);
    body.put("payload", payload != null ? payload : Map.of());
    if (traceTag != null) body.put("trace_tag", traceTag);
    return body;
  }
}
