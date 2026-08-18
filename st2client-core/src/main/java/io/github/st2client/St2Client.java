package io.github.st2client;

import io.github.st2client.auth.DefaultTokenProvider;
import io.github.st2client.auth.TokenProvider;
import io.github.st2client.config.ClientConfig;
import io.github.st2client.config.RetryPolicy;
import io.github.st2client.internal.http.AuthHeadersInterceptor;
import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.model.*;
import io.github.st2client.resource.*;
import io.github.st2client.stream.St2StreamClient;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.*;

/**
 * Entry point for the StackStorm Java SDK. Provides access to all StackStorm resource clients and
 * the streaming SSE client. Create instances via {@link St2ClientBuilder} obtained through {@code
 * St2Client.builder()...build()}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class St2Client implements Closeable {

  private static final Logger log = LoggerFactory.getLogger(St2Client.class);

  private final ClientConfig config;
  private final OkHttpClient okHttp;
  private final TokenProvider tokenProvider;
  private final boolean ownsOkHttpClient;

  private final St2HttpClient apiHttp;

  private final ActionClient actions;
  private final ExecutionClient executions;
  private final ResourceClient<Rule> rules;
  private final ResourceClient<Trigger> triggers;
  private final PackClient packs;
  private final KeyValueClient keys;
  private final ResourceClient<Token> tokens;
  private final ResourceClient<ApiKey> apiKeys;
  private final ResourceClient<TriggerType> triggerTypes;
  private final ResourceClient<Sensor> sensors;
  private final ResourceClient<Policy> policies;
  private final ResourceClient<PolicyType> policyTypes;
  private final ActionAliasClient actionAliases;
  private final InquiryClient inquiries;
  private final ResourceClient<RuleEnforcement> ruleEnforcements;
  private final TriggerInstanceClient triggerInstances;
  private final WebhookClient webhooks;
  private final ConfigClient configs;
  private final ResourceClient<ConfigSchema> configSchemas;
  private final ResourceClient<Timer> timers;
  private final ResourceClient<Trace> traces;
  private final ResourceClient<RunnerType> runnerTypes;

  private final AtomicBoolean closed = new AtomicBoolean(false);
  private volatile St2StreamClient streamClient;

  St2Client(
      ClientConfig config,
      OkHttpClient baseOkHttp,
      TokenProvider injectedTokenProvider,
      boolean ownsOkHttpClient,
      RetryPolicy retryPolicy) {
    this.config = config;
    this.ownsOkHttpClient = ownsOkHttpClient;
    this.tokenProvider =
        injectedTokenProvider != null
            ? injectedTokenProvider
            : new DefaultTokenProvider(config, baseOkHttp);
    this.okHttp =
        baseOkHttp
            .newBuilder()
            .addInterceptor(new AuthHeadersInterceptor(tokenProvider))
            .authenticator(new St2Authenticator(tokenProvider))
            .build();

    this.apiHttp = new St2HttpClient(config.getApiUrl(), this.okHttp, retryPolicy);
    St2HttpClient authHttp = new St2HttpClient(config.getAuthUrl(), this.okHttp, retryPolicy);

    this.tokens = new ResourceClient<>(ResourceDescriptor.TOKEN, authHttp);
    this.apiKeys = new ResourceClient<>(ResourceDescriptor.API_KEY, apiHttp);
    this.actions = new ActionClient(apiHttp);
    this.executions = new ExecutionClient(apiHttp);
    this.rules = new ResourceClient<>(ResourceDescriptor.RULE, apiHttp);
    this.triggers = new ResourceClient<>(ResourceDescriptor.TRIGGER, apiHttp);
    this.packs = new PackClient(apiHttp);
    this.keys = new KeyValueClient(apiHttp);
    this.triggerTypes = new ResourceClient<>(ResourceDescriptor.TRIGGER_TYPE, apiHttp);
    this.sensors = new ResourceClient<>(ResourceDescriptor.SENSOR, apiHttp);
    this.policies = new ResourceClient<>(ResourceDescriptor.POLICY, apiHttp);
    this.policyTypes = new ResourceClient<>(ResourceDescriptor.POLICY_TYPE, apiHttp);
    this.actionAliases = new ActionAliasClient(apiHttp);
    this.inquiries = new InquiryClient(apiHttp);
    this.ruleEnforcements = new ResourceClient<>(ResourceDescriptor.RULE_ENFORCEMENT, apiHttp);
    this.triggerInstances = new TriggerInstanceClient(apiHttp);
    this.webhooks = new WebhookClient(apiHttp);
    this.configs = new ConfigClient(apiHttp);
    this.configSchemas = new ResourceClient<>(ResourceDescriptor.CONFIG_SCHEMA, apiHttp);
    this.timers = new ResourceClient<>(ResourceDescriptor.TIMER, apiHttp);
    this.traces = new ResourceClient<>(ResourceDescriptor.TRACE, apiHttp);
    this.runnerTypes = new ResourceClient<>(ResourceDescriptor.RUNNER_TYPE, apiHttp);
  }

  public static St2ClientBuilder builder() {
    return new St2ClientBuilder();
  }

  public CompletableFuture<Void> authenticate() {
    return tokenProvider.refresh().thenAccept(t -> {});
  }

  /**
   * Authenticates with the given credentials via the StackStorm auth service.
   *
   * @param username the StackStorm username
   * @param password the StackStorm password
   * @throws IOException if the authentication request fails
   * @since 0.1.0
   */
  public void authenticate(String username, String password) throws IOException {
    tokenProvider.authenticate(username, password);
  }

  public UserInfo getUserInfo() throws IOException {
    try (Response r = apiHttp.get("/user", null)) {
      if (r.body() == null) {
        throw new IOException("Empty response body from /user");
      }

      return Resource.fromMap(
          Resource.readJson(r.body().string(), Resource.MAP_TYPE), UserInfo.class);
    }
  }

  // -- Accessors --

  public ActionClient actions() {
    return actions;
  }

  public ExecutionClient executions() {
    return executions;
  }

  public ResourceClient<Rule> rules() {
    return rules;
  }

  public ResourceClient<Trigger> triggers() {
    return triggers;
  }

  public PackClient packs() {
    return packs;
  }

  public KeyValueClient keys() {
    return keys;
  }

  public ResourceClient<Token> tokens() {
    return tokens;
  }

  public ResourceClient<ApiKey> apiKeys() {
    return apiKeys;
  }

  public ResourceReader<TriggerType> triggerTypes() {
    return triggerTypes;
  }

  public ResourceClient<Sensor> sensors() {
    return sensors;
  }

  public ResourceClient<Policy> policies() {
    return policies;
  }

  public ResourceReader<PolicyType> policyTypes() {
    return policyTypes;
  }

  public ActionAliasClient actionAliases() {
    return actionAliases;
  }

  public InquiryClient inquiries() {
    return inquiries;
  }

  public ResourceReader<RuleEnforcement> ruleEnforcements() {
    return ruleEnforcements;
  }

  public TriggerInstanceClient triggerInstances() {
    return triggerInstances;
  }

  public WebhookClient webhooks() {
    return webhooks;
  }

  public ConfigClient configs() {
    return configs;
  }

  public ResourceReader<ConfigSchema> configSchemas() {
    return configSchemas;
  }

  public ResourceReader<Timer> timers() {
    return timers;
  }

  public ResourceReader<Trace> traces() {
    return traces;
  }

  public ResourceReader<RunnerType> runnerTypes() {
    return runnerTypes;
  }

  public ClientConfig getConfig() {
    return config;
  }

  /**
   * Returns the high-level SSE streaming client for consuming events from {@code /stream}. The
   * instance is created lazily and reused for the lifetime of this client.
   *
   * @return the SSE stream client
   * @since 0.1.0
   */
  public St2StreamClient streamClient() {
    if (streamClient == null) {
      synchronized (this) {
        if (streamClient == null) {
          streamClient = new St2StreamClient(okHttp, config.getStreamUrl(), tokenProvider);
        }
      }
    }
    return streamClient;
  }

  @Override
  public void close() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }
    St2StreamClient sc = streamClient;
    if (sc != null) {
      sc.close();
    }
    executions.shutdownStatusMonitors();
    if (ownsOkHttpClient) {
      okHttp.dispatcher().executorService().shutdown();
      okHttp.connectionPool().evictAll();
    }
  }

  // -- 401 auto-retry Authenticator --

  /**
   * OkHttp {@link Authenticator} that intercepts 401 responses and attempts a token refresh before
   * retrying the request. Maximum one refresh attempt per request chain.
   *
   * @since 0.1.0
   */
  private static class St2Authenticator implements Authenticator {
    private final TokenProvider tp;

    St2Authenticator(TokenProvider tp) {
      this.tp = tp;
    }

    @Override
    public Request authenticate(Route route, @NotNull Response response) {
      if (responseCount(response) >= 2) return null;
      if (!tp.supportsTokenRefresh()) return null;

      tp.onUnauthorized();
      try {
        String newToken = tp.refresh().get(30, TimeUnit.SECONDS);
        if (newToken == null || newToken.isEmpty()) {
          return null;
        }
        return response
            .request()
            .newBuilder()
            .header(AuthHeadersInterceptor.HEADER_AUTH_TOKEN, newToken)
            .build();
      } catch (Exception e) {
        log.warn("Failed to refresh token during 401 retry", e);
        return null;
      }
    }

    private int responseCount(Response response) {
      int count = 1;
      while ((response = response.priorResponse()) != null && count < 10) count++;
      return count;
    }
  }
}
