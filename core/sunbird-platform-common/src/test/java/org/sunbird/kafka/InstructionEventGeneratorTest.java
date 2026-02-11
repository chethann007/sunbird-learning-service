package org.sunbird.kafka;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.MockedConstruction;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.response.ResponseCode;
import org.sunbird.common.ProjectUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

public class InstructionEventGeneratorTest {

  @BeforeClass
  public static void setUp() {
      // Set required properties to avoid errors during static init properties loading
      ProjectUtil.propertiesCache.saveConfigProperty("kafka_urls", "localhost:9092");
      ProjectUtil.propertiesCache.saveConfigProperty("kafka_linger_ms", "10");

      // Force static initialization of KafkaClient while mocks are active
      // This is needed if KafkaClient hasn't been initialized yet
      try (MockedConstruction<KafkaProducer> mockedProducer = mockConstruction(KafkaProducer.class);
           MockedConstruction<KafkaConsumer> mockedConsumer = mockConstruction(KafkaConsumer.class,
             (mock, context) -> {
               when(mock.listTopics()).thenReturn(new HashMap<String, List<PartitionInfo>>());
             })) {

           try {
               Class.forName(KafkaClient.class.getName());
           } catch (ClassNotFoundException e) {
               // Ignore if class not found, but RuntimeException if init fails
           } catch (ExceptionInInitializerError e) {
               // If it was already failed, we might be in trouble. But usually Class.forName is idempotent if success.
           } catch (NoClassDefFoundError e) {
               // This means previous init failed. We can't easily recover in same JVM if class is in error state.
               // But if running separately, this block ensures success.
           }
      }
  }

  @Test
  public void testPushInstructionEvent() throws Exception {
    try (MockedStatic<KafkaClient> mockedKafkaClient = mockStatic(KafkaClient.class)) {
      Map<String, Object> data = new HashMap<>();
      data.put("actor", new HashMap<>());
      data.put("context", new HashMap<>());
      data.put("object", new HashMap<>());
      data.put("edata", new HashMap<>());

      InstructionEventGenerator.pushInstructionEvent("test-topic", data);

      mockedKafkaClient.verify(() -> KafkaClient.send(anyString(), eq("test-topic")), times(1));
    }
  }

  @Test
  public void testPushInstructionEventWithKey() throws Exception {
    try (MockedStatic<KafkaClient> mockedKafkaClient = mockStatic(KafkaClient.class)) {
      Map<String, Object> data = new HashMap<>();

      InstructionEventGenerator.pushInstructionEvent("key", "test-topic", data);

      mockedKafkaClient.verify(() -> KafkaClient.send(eq("key"), anyString(), eq("test-topic")), times(1));
    }
  }

  @Test(expected = ProjectCommonException.class)
  public void testPushInstructionEventNullTopic() throws Exception {
      Map<String, Object> data = new HashMap<>();
      InstructionEventGenerator.pushInstructionEvent(null, data);
  }
}
