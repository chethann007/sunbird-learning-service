package org.sunbird.keycloak;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.http.HttpClientUtil;
import org.sunbird.request.RequestContext;
import org.sunbird.common.ProjectUtil;

@PrepareForTest({ProjectUtil.class, HttpClientUtil.class})
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("org.sunbird.common.ProjectUtil")
@PowerMockIgnore({
  "javax.management.*",
  "javax.net.ssl.*",
  "javax.security.*",
  "jdk.internal.reflect.*",
  "javax.crypto.*"
})
public class KeycloakUtilTest {

  @Before
  public void setup() {
    PowerMockito.mockStatic(HttpClientUtil.class);
    PowerMockito.mockStatic(ProjectUtil.class);
    when(HttpClientUtil.postFormData(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(RequestContext.class)))
        .thenReturn("{\"access_token\":\"accesstoken\"}");
    when(ProjectUtil.getConfigValue(Mockito.anyString())).thenReturn("anyString");
  }

  @Test
  public void testGetAdminAccessToken() throws Exception {
    String token = KeycloakUtil.getAdminAccessToken(new RequestContext(), "url");
    assertTrue(token.equals("accesstoken"));
  }

  @Test
  public void testGetAdminAccessTokenWithDomain() throws Exception {
    String token = KeycloakUtil.getAdminAccessTokenWithDomain(new RequestContext());
    assertTrue(token.equals("accesstoken"));
  }

  @Test
  public void testGetAdminAccessTokenWithoutDomain() throws Exception {
    String token = KeycloakUtil.getAdminAccessTokenWithoutDomain(new RequestContext());
    assertTrue(token.equals("accesstoken"));
  }
}
