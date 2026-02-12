package org.sunbird.keycloak.impl;

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.response.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.request.RequestContext;
import org.sunbird.keycloak.KeyCloakConnectionProvider;
import org.sunbird.keycloak.KeycloakRequiredActionLinkUtil;
import org.sunbird.keycloak.SSOManager;
import org.sunbird.keycloak.SSOServiceFactory;
import org.sunbird.common.ProjectUtil;
import org.sunbird.common.PropertiesCache;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  ProjectUtil.class,
  KeyCloakConnectionProvider.class,
  KeycloakRequiredActionLinkUtil.class,
  PropertiesCache.class,
  SSOServiceFactory.class,
  KeyCloakServiceImpl.class
})
@SuppressStaticInitializationFor({
  "org.sunbird.common.ProjectUtil",
  "org.sunbird.keycloak.KeyCloakConnectionProvider"
})
@PowerMockIgnore({
  "javax.management.*",
  "javax.net.ssl.*",
  "javax.security.*",
  "jdk.internal.reflect.*",
  "javax.crypto.*"
})
public class KeyCloakServiceImplTest {

  private SSOManager keyCloakService;
  private static Map<String, String> userId = new HashMap<>();
  private static final String userName = UUID.randomUUID().toString().replaceAll("-", "");

  private static UsersResource usersRes = mock(UsersResource.class);
  private static UserResource userRes = mock(UserResource.class);
  private static Keycloak kcp;
  private static RealmResource realmRes;
  private static UserRepresentation userRep;

  @Before
  public void setUp() throws Exception {
    PowerMockito.mockStatic(PropertiesCache.class);
    PropertiesCache propertiesCache = mock(PropertiesCache.class);
    when(PropertiesCache.getInstance()).thenReturn(propertiesCache);
    PowerMockito.when(propertiesCache.getProperty(Mockito.anyString())).thenReturn("anyString");

    PowerMockito.mockStatic(ProjectUtil.class);
    PowerMockito.when(ProjectUtil.getConfigValue(Mockito.anyString())).thenReturn("somestring");

    kcp = mock(Keycloak.class);
    realmRes = mock(RealmResource.class);
    userRep = mock(UserRepresentation.class);
    Response response = mock(Response.class);

    PowerMockito.mockStatic(KeyCloakConnectionProvider.class);
    KeyCloakConnectionProvider.SSO_REALM = "sunbird";
    doReturn(kcp).when(KeyCloakConnectionProvider.class, "getConnection");

    // Setup Keycloak mocks
    doReturn(realmRes).when(kcp).realm(Mockito.anyString());
    doReturn(usersRes).when(realmRes).users();
    doReturn(201).when(response).getStatus();
    doReturn("userdata").when(response).getHeaderString(Mockito.eq("Location"));

    doReturn(userRes).when(usersRes).get(Mockito.anyString());
    doReturn(userRep).when(userRes).toRepresentation();
    doNothing().when(userRes).update(Mockito.any(UserRepresentation.class));
    doNothing().when(userRes).remove();

    Map<String, Object> map = new HashMap<>();
    map.put(JsonKey.LAST_LOGIN_TIME, Arrays.asList(String.valueOf(System.currentTimeMillis())));
    doReturn(map).when(userRep).getAttributes();
    when(userRep.getUsername()).thenReturn("userName");

    // Initialize service
    keyCloakService = SSOServiceFactory.getInstance();

    userId.put(JsonKey.USER_ID, UUID.randomUUID().toString());
  }

  @Test
  public void testNewInstanceSucccess() {
    Exception exp = null;
    try {
      Constructor<SSOServiceFactory> constructor = SSOServiceFactory.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      SSOServiceFactory application = constructor.newInstance();
      Assert.assertNotNull(application);
    } catch (Exception e) {
      exp = e;
    }
    Assert.assertNull(exp);
  }

  @Test(expected = ProjectCommonException.class)
  public void testRemoveUserFailure() {
    // If we throw exception during removal
    doThrow(new RuntimeException("Error")).when(usersRes).get(Mockito.anyString());
    Map<String, Object> request = new HashMap<>();
    request.put(JsonKey.USER_ID, "123");
    keyCloakService.removeUser(request, null);
  }

  @Test
  public void testRemoveUserSuccess() {
      Map<String, Object> request = new HashMap<>();
      request.put(JsonKey.USER_ID, "123");
      String result = keyCloakService.removeUser(request, null);
      Assert.assertEquals(JsonKey.SUCCESS, result);
  }

  @Test
  public void testDeactivateUserSuccess() {
      Map<String, Object> request = new HashMap<>();
      request.put(JsonKey.USER_ID, "123");
      // userRep.isEnabled() is false by default mock
      String result = keyCloakService.deactivateUser(request, null);
      Assert.assertEquals(JsonKey.SUCCESS, result);
  }

  @Test
  public void testActivateUserSuccess() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put(JsonKey.USER_ID, userId.get(JsonKey.USER_ID));
    String response = keyCloakService.activateUser(reqMap, null);
    Assert.assertEquals(JsonKey.SUCCESS, response);
  }

  @Test
  public void testActivateUserFailureWithEmptyUserId() {
    Map<String, Object> reqMap = new HashMap<>();
    reqMap.put(JsonKey.USER_ID, "");
    try {
      keyCloakService.activateUser(reqMap, null);
    } catch (ProjectCommonException e) {
      Assert.assertEquals(ResponseCode.invalidParameterValue.getErrorCode(), e.getErrorCode());
      Assert.assertEquals(ResponseCode.CLIENT_ERROR.getResponseCode(), e.getErrorResponseCode());
    }
  }

  @Test
  public void testUpdatePassword() throws Exception {
    boolean updated = keyCloakService.updatePassword(userId.get(JsonKey.USER_ID), "password", null);
    Assert.assertTrue(updated);
  }

  @Test
  public void testRemovePII() {
    boolean piiRemoved = keyCloakService.removePII(userId.get(JsonKey.USER_ID), new RequestContext());
    Assert.assertTrue(piiRemoved);
  }

  @Test(expected = ProjectCommonException.class)
  public void testVerifyTokenFailure() {
    keyCloakService.verifyToken(
        "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI5emhhVnZDbl81OEtheHpldHBzYXNZQ2lEallkemJIX3U2LV93SDk4SEc0In0.eyJqdGkiOiI5ZmQzNzgzYy01YjZmLTQ3OWQtYmMzYy0yZWEzOGUzZmRmYzgiLCJleHAiOjE1MDUxMTQyNDYsIm5iZiI6MCwiaWF0IjoxNTA1MTEzNjQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoic2VjdXJpdHktYWRtaW4tY29uc29sZSIsInN1YiI6ImIzYTZkMTY4LWJjZmQtNDE2MS1hYzVmLTljZjYyODIyNzlmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6InNlY3VyaXR5LWFkbWluLWNvbnNvbGUiLCJub25jZSI6ImMxOGVlMDM2LTAyMWItNGVlZC04NWVhLTc0MjMyYzg2ZmI4ZSIsImF1dGhfdGltZSI6MTUwNTExMzY0Niwic2Vzc2lvbl9zdGF0ZSI6ImRiZTU2NDlmLTY4MDktNDA3NS05Njk5LTVhYjIyNWMwZTkyMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZXNvdXJjZV9hY2Nlc3MiOnt9LCJuYW1lIjoiTWFuemFydWwgaGFxdWUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0MTIzNDU2NyIsImdpdmVuX25hbWUiOiJNYW56YXJ1bCBoYXF1ZSIsImVtYWlsIjoidGVzdDEyM0B0LmNvbSJ9.Xdjqe16MSkiR94g-Uj_pVZ2L3gnIdKpkJ6aB82W_w_c3yEmx1mXYBdkxe4zMz3ks4OX_PWwSFEbJECHcnujUwF6Ula0xtXTfuESB9hFyiWHtVAhuh5UlCCwPnsihv5EqK6u-Qzo0aa6qZOiQK3Zo7FLpnPUDxn4yHyo3mRZUiWf76KTl8PhSMoXoWxcR2vGW0b-cPixILTZPV0xXUZoozCui70QnvTgOJDWqr7y80EWDkS4Ptn-QM3q2nJlw63mZreOG3XTdraOlcKIP5vFK992dyyHlYGqWVzigortS9Ah4cprFVuLlX8mu1cQvqHBtW-0Dq_JlcTMaztEnqvJ6XA",
        null);
  }
}
