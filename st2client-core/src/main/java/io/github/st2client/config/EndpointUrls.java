package io.github.st2client.config;

import io.github.st2client.exception.ConfigurationException;
import io.github.st2client.internal.http.UrlPaths;

import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.HttpUrl;

/**
 * Derives api / auth / stream URLs from a StackStorm base URL.
 *
 * <p>Host-only URLs keep the classic split-port layout ({@code :9101/v1}, {@code :9100}, {@code
 * :9102/v1}). URLs that already include a path are treated as ingress and mapped onto {@code
 * /api/v1}, {@code /auth}, and {@code /stream/v1}.
 */
final class EndpointUrls {

  private EndpointUrls() {}

  static Derived derive(String baseUrl, String apiVersion) {
    HttpUrl parsed = HttpUrl.parse(baseUrl);
    if (parsed == null) {
      throw new ConfigurationException("Invalid baseUrl: " + baseUrl);
    }
    URI uri = toUri(baseUrl);
    if (hasMeaningfulPath(uri)) {
      return pathBased(parsed, apiVersion);
    }
    return portBased(parsed, apiVersion);
  }

  private static Derived portBased(HttpUrl parsed, String apiVersion) {
    HttpUrl api =
        parsed
            .newBuilder()
            .port(ClientConfig.DEFAULT_API_PORT)
            .encodedPath("/" + apiVersion)
            .build();
    HttpUrl auth =
        parsed.newBuilder().port(ClientConfig.DEFAULT_AUTH_PORT).encodedPath("/").build();
    HttpUrl stream =
        parsed
            .newBuilder()
            .port(ClientConfig.DEFAULT_STREAM_PORT)
            .encodedPath("/" + apiVersion)
            .build();
    return new Derived(strip(api), strip(auth), strip(stream));
  }

  private static Derived pathBased(HttpUrl parsed, String apiVersion) {
    HttpUrl.Builder apiBuilder = parsed.newBuilder();
    if (!endsWithVersion(parsed, apiVersion)) {
      apiBuilder.addPathSegment(apiVersion);
    }
    HttpUrl auth = parsed.newBuilder().encodedPath("/auth").build();
    HttpUrl stream = parsed.newBuilder().encodedPath("/stream/" + apiVersion).build();
    return new Derived(strip(apiBuilder.build()), strip(auth), strip(stream));
  }

  private static boolean endsWithVersion(HttpUrl url, String apiVersion) {
    java.util.List<String> segments = url.pathSegments();
    return !segments.isEmpty() && apiVersion.equals(segments.get(segments.size() - 1));
  }

  private static boolean hasMeaningfulPath(URI uri) {
    String path = uri.getPath();
    return path != null && !path.isEmpty() && !"/".equals(path);
  }

  private static URI toUri(String baseUrl) {
    try {
      return new URI(baseUrl);
    } catch (URISyntaxException e) {
      throw new ConfigurationException("Invalid baseUrl: " + baseUrl, e);
    }
  }

  private static String strip(HttpUrl url) {
    return UrlPaths.stripTrailingSlash(url.toString());
  }

  record Derived(String apiUrl, String authUrl, String streamUrl) {}
}
