package org.sunbird.keycloak;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.common.PropertiesCache;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
  KeyCloakConnectionProvider.class,
  KeycloakBuilder.class,
  ResteasyClientBuilderImpl.class,
  PropertiesCache.class
})
@SuppressStaticInitializationFor("org.sunbird.keycloak.KeyCloakConnectionProvider")
@PowerMockIgnore({
  "javax.management.*",
  "javax.net.ssl.*",
  "javax.security.*",
  "jdk.internal.reflect.*",
  "javax.crypto.*"
})
public class KeyCloakConnectionProviderTest {

  @Before
  public void setUp() {
    PowerMockito.mockStatic(PropertiesCache.class);
    PropertiesCache propertiesCache = mock(PropertiesCache.class);
    when(PropertiesCache.getInstance()).thenReturn(propertiesCache);
    when(propertiesCache.getProperty(Mockito.anyString())).thenReturn("anyString");
    // Only return int string for pool size
    when(propertiesCache.getProperty(JsonKey.SSO_POOL_SIZE)).thenReturn("10");

    // Inject mock logger
    Whitebox.setInternalState(KeyCloakConnectionProvider.class, "logger", mock(LoggerUtil.class));
    // Inject mock cache
    Whitebox.setInternalState(KeyCloakConnectionProvider.class, "cache", propertiesCache);
  }

  @Test
  public void testInitialiseConnectionSuccess() throws Exception {
    PowerMockito.mockStatic(KeycloakBuilder.class);
    KeycloakBuilder builder = mock(KeycloakBuilder.class);
    when(KeycloakBuilder.builder()).thenReturn(builder);
    when(builder.serverUrl(Mockito.anyString())).thenReturn(builder);
    when(builder.realm(Mockito.anyString())).thenReturn(builder);
    when(builder.username(Mockito.anyString())).thenReturn(builder);
    when(builder.password(Mockito.anyString())).thenReturn(builder);
    when(builder.clientId(Mockito.anyString())).thenReturn(builder);
    when(builder.clientSecret(Mockito.anyString())).thenReturn(builder);
    when(builder.resteasyClient(Mockito.any())).thenReturn(builder);

    Keycloak keycloak = mock(Keycloak.class);
    when(builder.build()).thenReturn(keycloak);

    ResteasyClientBuilderImpl resteasyClientBuilder = mock(ResteasyClientBuilderImpl.class);
    PowerMockito.whenNew(ResteasyClientBuilderImpl.class).withNoArguments().thenReturn(resteasyClientBuilder);
    when(resteasyClientBuilder.connectionPoolSize(Mockito.anyInt())).thenReturn(resteasyClientBuilder);
    when(resteasyClientBuilder.build()).thenReturn(null); // The result is passed to resteasyClient(), checking if null causes issue?

    // KeyCloakConnectionProvider calls new ResteasyClientBuilderImpl() directly.

    Keycloak result = KeyCloakConnectionProvider.initialiseConnection();
    assertNotNull(result);
  }
}
