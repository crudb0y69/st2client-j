package io.github.st2client.internal.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.Test;

/** Tests that custom CA certificates are merged into the system trust manager. */
class SslTrustTest {

  private static final String LOCALHOST_CA_DER_B64 =
      "MIIDGDCCAgCgAwIBAgIQNsD5VeljT7VNocZoNpYXiDANBgkqhkiG9w0BAQsFADAUMRIwEAYDVQQDDAlsb2NhbGhvc3QwHhcNMjYwNjI1MTI1MDQ0WhcNMjcwNjI1MTMwMDQzWjAUMRIwEAYDVQQDDAlsb2NhbGhvc3QwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDGE4of0JF5L+9SmHhQipCX1Lhre/6cKcxzczH+BwkT58OQJcEbam5ew9qvjIlFlGKgAJeRL+iAxHpnWjiN7urWUsCUS2fsewbrlRRNjp6A1Ca2Wec++6i3vllheImqf00d1jJHUn0LI+Z1kAyHP5GqCefD7ud6rJuBrPskF5pJ59Sozrlr9jZYrQ4u9o4oBDCscKjqf7zIlPLsjuIj22gL21w3e35TPsketdS+V0ObbBI6G5O4N+n6p59aWLdIbhjcB4UOlBkWA9agqhsnGK2JzXY3T5pgpMRn7L3vAziAw2AZGiHr9oItRi3dg3a/DVRGwLvX3VuD3OUqV0Dbg1TZAgMBAAGjZjBkMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwEwFAYDVR0RBA0wC4IJbG9jYWxob3N0MB0GA1UdDgQWBBTQz6g5Vhak2GCSw2P7AJOw9oEYrTANBgkqhkiG9w0BAQsFAAOCAQEAbxEe/TrMclNQc6tO0VBzWVWjr64Qt3EAUf4/Cl9MTjN+hqzgzqJpDrX5fm6JWeNBfAJnjafywk2qYNKqe4zew40aUlPBYPzgimD598RaJFZvbgPJ1KheP31pBBnJR718uNcVjFVKF57NOm/7Qsdz4Tf7P3FiDaudqW13iQ0HbsFo3W8olpsvrtvbeg4I9oB2wNXTpQ15S5dv9pNe4Kvg/aVa1NfltEn9JbvWMh9ekUHC/R4FBy0eG4OHIkoZAepsPfsOBOmmUq/2G48G78xOJAXXuQ7VCXEtN3oMk5Sd++slRwcALGPqKjNRyS1dLCk3Fr0lwBsldK2lTitUg9fNrw==";

  @Test
  void shouldIncludeCustomCaInAcceptedIssuers() throws Exception {
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    X509Certificate ca =
        (X509Certificate)
            cf.generateCertificate(
                new ByteArrayInputStream(Base64.getDecoder().decode(LOCALHOST_CA_DER_B64)));

    X509TrustManager trustManager = SslTrust.systemPlus(ca);

    assertThat(trustManager.getAcceptedIssuers()).anyMatch(ca::equals);
  }
}
