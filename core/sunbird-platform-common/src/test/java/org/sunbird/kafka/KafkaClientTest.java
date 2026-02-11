package org.sunbird.kafka;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.sunbird.common.ProjectUtil;

public class KafkaClientTest {

  @BeforeClass
  public static void setUp() {
      // Set required properties to avoid errors during static init properties loading
      ProjectUtil.propertiesCache.saveConfigProperty("kafka_urls", "localhost:9092");
      ProjectUtil.propertiesCache.saveConfigProperty("kafka_linger_ms", "10");

      // Force static initialization of KafkaClient while mocks are active
      try (MockedConstruction<KafkaProducer> mockedProducer = mockConstruction(KafkaProducer.class);
           MockedConstruction<KafkaConsumer> mockedConsumer = mockConstruction(KafkaConsumer.class,
             (mock, context) -> {
               when(mock.listTopics()).thenReturn(new HashMap<String, List<PartitionInfo>>());
             })) {

           try {
               Class.forName(KafkaClient.class.getName());
           } catch (ClassNotFoundException e) {
               throw new RuntimeException(e);
           }
      }
  }

  @After
  public void tearDown() {
      // Reset static fields if needed to avoid pollution
      // Since we modified producer and topics in testSend, we should probably reset them or let subsequent tests handle it.
      // But other tests use mockConstruction which overrides constructor, but doesn't change the static fields if initialization already happened.
      // So resetting to null might trigger re-initialization if we force it? No, static block runs once.
      // We can't re-run static block.
      // So we should leave them in a valid mock state.
      // testSend sets mocks. Other tests (createProducer/Consumer) don't use the static fields.
      // testStaticInitializationAndGetters checks getters.

      // If testSend runs, it replaces producer.
      // If testStaticInitializationAndGetters runs after, it gets the replaced producer. That's fine as long as it's not null.
  }

  @Test
  public void testStaticInitializationAndGetters() {
      // Since initialized in BeforeClass, these should be non-null (and are mocks)
      assertNotNull(KafkaClient.getProducer());
      assertNotNull(KafkaClient.getConsumer());
  }

  @Test
  public void testSend() throws Exception {
    java.lang.reflect.Field producerField = KafkaClient.class.getDeclaredField("producer");
    producerField.setAccessible(true);
    KafkaProducer<String, String> mockProducer = mock(KafkaProducer.class);
    producerField.set(null, mockProducer);

    java.lang.reflect.Field topicsField = KafkaClient.class.getDeclaredField("topics");
    topicsField.setAccessible(true);
    Map<String, List<PartitionInfo>> topics = new HashMap<>();
    topics.put("test-topic", null);
    topicsField.set(null, topics);

    KafkaClient.send("message", "test-topic");
    verify(mockProducer, times(1)).send(any(ProducerRecord.class));

    KafkaClient.send("key", "message", "test-topic");
    verify(mockProducer, times(2)).send(any(ProducerRecord.class));
  }

  @Test
  public void testCreateProducer() {
      try (MockedConstruction<KafkaProducer> mockedProducer = mockConstruction(KafkaProducer.class)) {
          KafkaClient.createProducer("localhost:9092", "client");
      }
  }

  @Test
  public void testCreateConsumer() {
      try (MockedConstruction<KafkaConsumer> mockedConsumer = mockConstruction(KafkaConsumer.class)) {
          KafkaClient.createConsumer("localhost:9092", "client");
      }
  }
}
