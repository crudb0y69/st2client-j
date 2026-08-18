package io.github.st2client.internal.http;

import io.github.st2client.auth.TokenProvider;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp interceptor that injects authentication headers from a {@link TokenProvider} into every
 * outgoing request.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class AuthHeadersInterceptor implements Interceptor {

  /** HTTP header name for the StackStorm authentication token. */
  public static final String HEADER_AUTH_TOKEN = "X-Auth-Token";

  /** HTTP header name for the StackStorm API key. */
  public static final String HEADER_API_KEY = "St2-Api-Key";

  private final TokenProvider tokenProvider;

  /**
   * Creates an AuthHeadersInterceptor that reads tokens from the given provider.
   *
   * @param tokenProvider the provider that supplies authentication tokens
   */
  public AuthHeadersInterceptor(TokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  /**
   * Adds authentication headers (token and/or API key) to the outgoing request and proceeds with
   * the interceptor chain.
   *
   * @param chain the interceptor chain
   * @return the response from the next interceptor or the HTTP call
   * @throws IOException if an I/O error occurs during the request
   */
  @NotNull
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request().newBuilder();
    tokenProvider.getToken().ifPresent(t -> builder.header(HEADER_AUTH_TOKEN, t));
    tokenProvider.getApiKey().ifPresent(k -> builder.header(HEADER_API_KEY, k));
    return chain.proceed(builder.build());
  }
}
