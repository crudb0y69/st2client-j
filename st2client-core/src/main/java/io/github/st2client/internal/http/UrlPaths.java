package io.github.st2client.internal.http;

import okhttp3.HttpUrl;

/**
 * Builds percent-encoded URL paths for StackStorm API segments (refs, ids, key names). Provides
 * utility methods for safe URL construction.
 *
 * @author crudb0y69
 * @since 0.1.0
 */
public final class UrlPaths {

  private UrlPaths() {}

  /**
   * Joins path segments into an encoded path beginning with {@code /}. Each segment is
   * percent-encoded using OkHttp's {@link HttpUrl.Builder}.
   *
   * @param segments the path segments to join
   * @return the encoded path string
   * @throws IllegalArgumentException if any segment is null or empty
   */
  public static String join(String... segments) {
    HttpUrl.Builder builder = HttpUrl.get("http://placeholder").newBuilder();
    for (String segment : segments) {
      if (segment == null) {
        throw new IllegalArgumentException("Path segment must not be null");
      }
      if (segment.isEmpty()) {
        throw new IllegalArgumentException("Path segment must not be null or empty");
      }
      builder.addPathSegment(segment);
    }
    return builder.build().encodedPath();
  }

  /**
   * Strips a single trailing slash from a URL string, if present.
   *
   * @param url the URL string to process
   * @return the URL without a trailing slash, or null if the input is null
   */
  public static String stripTrailingSlash(String url) {
    if (url == null) return null;
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }
}
