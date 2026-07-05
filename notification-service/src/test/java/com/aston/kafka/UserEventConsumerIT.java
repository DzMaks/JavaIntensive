package com.aston.kafka;

import com.aston.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class UserEventConsumerIT {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private EmailService emailService;

    @Test
    void shouldConsumeKafkaEventAndSendEmail() {

        String eventJson = """
        {
          "email": "kafka@test.com",
          "event": "CREATED"
        }
        """;

        kafkaTemplate.send("user-events", eventJson);

        // даём Kafka consumer время обработать сообщение
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(emailService, times(1))
                .sendCreatedMessage("kafka@test.com");
    }
}