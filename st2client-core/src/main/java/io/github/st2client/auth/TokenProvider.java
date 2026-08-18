package io.github.st2client.auth;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Defines the contract for StackStorm authentication token management. Implementations handle token
 * retrieval, refresh, and lifecycle callbacks for authenticating with the StackStorm API.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public interface TokenProvider {

  /**
   * Returns the current authentication token, if available.
   *
   * @return an Optional containing the current token, or empty if not set
   */
  Optional<String> getToken();

  /**
   * Returns the API key for authentication, if configured.
   *
   * @return an Optional containing the API key, or empty if not set
   */
  Optional<String> getApiKey();

  /**
   * Overrides the current active token, for example after explicit authentication.
   *
   * @param token the new authentication token to use
   */
  void setToken(String token);

  /**
   * Initiates a token refresh asynchronously. The returned future completes with the new token
   * value upon success.
   *
   * @return a CompletableFuture that completes with the refreshed token
   */
  CompletableFuture<String> refresh();

  /**
   * Callback invoked when an API request receives a 401 Unauthorized response. Implementations
   * should clear the cached token to force re-authentication.
   */
  void onUnauthorized();

  /**
   * Authenticates with explicit credentials, bypassing configuration. The obtained token is stored
   * and used for all subsequent requests.
   *
   * @param username the username to authenticate with
   * @param password the password to authenticate with
   * @throws IOException if a network or authentication error occurs
   */
  default void authenticate(String username, String password) throws IOException {
    throw new UnsupportedOperationException("authenticate() not implemented");
  }

  /**
   * Returns whether {@link #refresh()} can obtain a new token for 401 retry. Returns {@code false}
   * for API key-based authentication.
   *
   * @return true if token refresh is supported, false otherwise
   */
  default boolean supportsTokenRefresh() {
    return true;
  }
}
