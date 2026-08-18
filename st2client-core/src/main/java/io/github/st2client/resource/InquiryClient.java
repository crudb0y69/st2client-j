package io.github.st2client.resource;

import io.github.st2client.internal.http.St2HttpClient;
import io.github.st2client.internal.http.UrlPaths;
import io.github.st2client.model.Inquiry;
import io.github.st2client.model.Resource;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;

/**
 * Resource manager for StackStorm Inquiries. Provides CRUD operations as well as {@code respond()}
 * and {@code respondAsync()}.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public class InquiryClient extends ResourceClient<Inquiry> {

  /**
   * Constructs a new InquiryClient.
   *
   * @param http the http
   * @since 0.1.0
   */
  public InquiryClient(St2HttpClient http) {
    super(ResourceDescriptor.INQUIRY, http);
  }

  /**
   * Responds to an inquiry
   *
   * @param inquiryId the inquiryId
   * @param response the response
   * @return the responded inquiry
   * @throws IOException if the operation fails
   * @since 0.1.0
   */
  public Inquiry respond(String inquiryId, Map<String, Object> response) throws IOException {
    try (Response r = http.put(UrlPaths.join("inquiries", inquiryId, "respond"), response)) {
      return Resource.fromMap(
          Resource.readJson(r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
          Inquiry.class);
    }
  }

  /**
   * Asynchronously responds to an inquiry
   *
   * @param inquiryId the inquiryId
   * @param response the response
   * @return a CompletableFuture that completes with the result
   * @since 0.1.0
   */
  public CompletableFuture<Inquiry> respondAsync(String inquiryId, Map<String, Object> response) {
    return http.putAsync(UrlPaths.join("inquiries", inquiryId, "respond"), response)
        .thenApply(
            r -> {
              try (r) {
                return Resource.fromMap(
                    Resource.readJson(
                        r.body() != null ? r.body().string() : "{}", Resource.MAP_TYPE),
                    Inquiry.class);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
