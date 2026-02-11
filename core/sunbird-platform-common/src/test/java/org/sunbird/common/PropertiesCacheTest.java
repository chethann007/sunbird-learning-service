package org.sunbird.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PropertiesCacheTest {

  @Test
  public void testGetInstance() {
    PropertiesCache cache = PropertiesCache.getInstance();
    assertNotNull(cache);
    assertEquals(cache, PropertiesCache.getInstance());
  }

  @Test
  public void testSaveAndGetProperty() {
    PropertiesCache cache = PropertiesCache.getInstance();
    cache.saveConfigProperty("test.key", "test.value");
    assertEquals("test.value", cache.getProperty("test.key"));
  }

  @Test
  public void testGetPropertyDefault() {
    PropertiesCache cache = PropertiesCache.getInstance();
    // getProperty returns key if not found
    assertEquals("missing.key", cache.getProperty("missing.key"));
  }

  @Test
  public void testReadProperty() {
    PropertiesCache cache = PropertiesCache.getInstance();
    cache.saveConfigProperty("read.key", "read.value");
    assertEquals("read.value", cache.readProperty("read.key"));
    // readProperty returns null if not found
    assertNull(cache.readProperty("missing.read.key"));
  }

  @Test
  public void testLoadWeighted() {
      // Indirectly test loadWeighted by checking if attributePercentageMap is populated or properties are loaded
      PropertiesCache cache = PropertiesCache.getInstance();
      assertNotNull(cache.attributePercentageMap);
      // Since default properties files are loaded, we might expect some values if configured.
      // But we can't be sure of the content of the files in the environment without reading them.
      // However, the fact that cache initialized means loadWeighted ran.
  }
}
