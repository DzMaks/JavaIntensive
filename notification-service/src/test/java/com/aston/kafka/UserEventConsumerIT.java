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
    void shouldConsumeCreatedEventAndSendCreatedEmail() throws Exception {

        String eventJson = """
                {
                  "email": "kafka@test.com",
                  "event": "CREATED"
                }
                """;

        kafkaTemplate.send("user-events", eventJson);

        Thread.sleep(1000);

        verify(emailService, times(1))
                .sendCreatedMessage("kafka@test.com");

        verify(emailService, never())
                .sendDeletedMessage(anyString());
    }

    @Test
    void shouldConsumeDeletedEventAndSendDeletedEmail() throws Exception {

        String eventJson = """
                {
                  "email": "kafka@test.com",
                  "event": "DELETED"
                }
                """;

        kafkaTemplate.send("user-events", eventJson);

        Thread.sleep(1000);

        verify(emailService, times(1))
                .sendDeletedMessage("kafka@test.com");

        verify(emailService, never())
                .sendCreatedMessage(anyString());
    }
}