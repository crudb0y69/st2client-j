package io.github.st2client.internal.http;

import io.github.st2client.exception.ConfigurationException;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Builds an {@link X509TrustManager} that trusts the JVM default issuers plus a custom CA.
 *
 * @since 0.1.0
 */
public final class SslTrust {

  private SslTrust() {}

  /**
   * Creates a trust manager that accepts the default system CAs and the given custom CA.
   *
   * @param caCert the additional CA certificate
   * @return a composite trust manager
   */
  public static X509TrustManager systemPlus(X509Certificate caCert) {
    try {
      TrustManagerFactory defaultTmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      defaultTmf.init((KeyStore) null);
      X509TrustManager defaultTm = (X509TrustManager) defaultTmf.getTrustManagers()[0];

      KeyStore custom = KeyStore.getInstance(KeyStore.getDefaultType());
      custom.load(null, null);
      for (X509Certificate issuer : defaultTm.getAcceptedIssuers()) {
        custom.setCertificateEntry(
            issuer.getSubjectX500Principal().getName() + issuer.hashCode(), issuer);
      }
      custom.setCertificateEntry("st2-custom-ca", caCert);

      TrustManagerFactory mergedTmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      mergedTmf.init(custom);
      return (X509TrustManager) mergedTmf.getTrustManagers()[0];
    } catch (Exception e) {
      throw new ConfigurationException("Failed to merge custom CA into trust store", e);
    }
  }
}
