package org.sunbird.keycloak.impl;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.security.PublicKey;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.keycloak.KeyCloakConnectionProvider;
import org.sunbird.common.PropertiesCache;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PrepareForTest({
  HttpClientBuilder.class,
  CloseableHttpClient.class,
  HttpGet.class,
  CloseableHttpResponse.class,
  HttpResponse.class,
  HttpEntity.class,
  EntityUtils.class,
  PropertiesCache.class
})
@PowerMockIgnore({
  "javax.management.*",
  "javax.net.ssl.*",
  "javax.security.*",
  "jdk.internal.reflect.*",
  "javax.crypto.*"
})
public class KeyCloakRsaKeyFetcherTest {

  public static final String FALSE_REALM = "false-realm";
  private static final HttpClientBuilder httpClientBuilder =
      PowerMockito.mock(HttpClientBuilder.class);
  private static CloseableHttpClient client = null;
  private static CloseableHttpResponse response;
  private static HttpEntity httpEntity;

  @Before
  public void setUp() throws Exception {
    PowerMockito.mockStatic(PropertiesCache.class);
    PropertiesCache propertiesCache = mock(PropertiesCache.class);
    when(PropertiesCache.getInstance()).thenReturn(propertiesCache);
    PowerMockito.when(propertiesCache.getProperty(Mockito.anyString())).thenReturn("anyString");

    client = PowerMockito.mock(CloseableHttpClient.class);
    PowerMockito.mockStatic(HttpClientBuilder.class);
    when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
    when(httpClientBuilder.build()).thenReturn(client);
    httpEntity = PowerMockito.mock(HttpEntity.class);
    PowerMockito.mockStatic(EntityUtils.class);
  }

  @Test
  public void testGetPublicKeyFromKeyCloakSuccess() throws Exception {

    response = PowerMockito.mock(CloseableHttpResponse.class);
    when(client.execute(Mockito.any(HttpGet.class))).thenReturn(response);
    when(response.getEntity()).thenReturn(httpEntity);

    String jsonString =
        "{\"keys\":[{\"kid\":\"YOw4KbDjM0_HIdGkf_QhRfKc9qHc4W_8Bni91nKFyck\",\"kty\":\"RSA\",\"alg\":\"RS256\",\"use\":\"sig\",\"n\":\""
            + "5OwCfx4UZTUfUDSBjOg65HuE4ReOg9GhZyoDJNqbWFrsY3dz7C12lmM3rewBHoY0F5_KW0A7rniS9LcqDg2RODvV8pRtJZ_Ge-jsnPMBY5nDJeEW35PH9ewaBhbY3Dj0bZQda2KdHGwiQ"
            + "zItMT4vw0uITKsFq9o1bcYj0QvPq10AE_wOx3T5xsysuTTkcvQ6evbbs6P5yz_SHhQFRTk7_ZhMwhBeTolvg9wF4yl4qwr220A1ORsLAwwydpmfMHU9RD97nzHDlhXTBAOhDoA3Z3wA8KG6V"
            + "i3LxqTLNRVS4hgq310fHzWfCX7shFQxygijW9zit-X1WVXaS1NxazuLJw\",\"e\":\"AQAB\"}]}";

    when(EntityUtils.toString(httpEntity)).thenReturn(jsonString);

    // Assuming SSO_URL and SSO_REALM are accessible or we pass dummy values
    String ssoUrl = "http://localhost:8080/auth";
    String ssoRealm = "sunbird";

    PublicKey key =
        new KeyCloakRsaKeyFetcher()
            .getPublicKeyFromKeyCloak(ssoUrl, ssoRealm);

    Assert.assertNotNull(key);
  }

  @Test
  public void testGetPublicKeyFromKeyCloakFailure() throws Exception {
    // Simulate failure by returning null or throwing exception or returning invalid JSON
    response = PowerMockito.mock(CloseableHttpResponse.class);
    when(client.execute(Mockito.any(HttpGet.class))).thenReturn(response);
    when(response.getEntity()).thenReturn(null);

    PublicKey key =
        new KeyCloakRsaKeyFetcher()
            .getPublicKeyFromKeyCloak("http://localhost:8080/auth", FALSE_REALM);

    Assert.assertNull(key);
  }

  @Test
  public void testGetPublicKeyFromKeyCloakException() throws Exception {
      when(client.execute(Mockito.any(HttpGet.class))).thenThrow(new RuntimeException("Connection failed"));

      PublicKey key =
              new KeyCloakRsaKeyFetcher()
                  .getPublicKeyFromKeyCloak("http://localhost:8080/auth", FALSE_REALM);

      Assert.assertNull(key);
  }
}
