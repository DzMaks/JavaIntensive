package com.aston.kafka.consumer;

import com.aston.common.event.EventType;
import com.aston.common.event.UserEvent;
import com.aston.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    private final EmailService emailService;

    public UserEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void listen(UserEvent event) {
        log.info("Received Kafka event: {}", event);

        if (event.getEvent() == EventType.CREATED) {
            emailService.sendCreatedMessage(event.getEmail());
        } else if (event.getEvent() == EventType.DELETED) {
            emailService.sendDeletedMessage(event.getEmail());
        }
    }
}