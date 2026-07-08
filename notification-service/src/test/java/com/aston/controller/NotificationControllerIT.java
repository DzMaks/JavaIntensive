package com.aston.controller;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendEmail_shouldReturnOkAndCallMailSender() throws Exception {

        String requestJson = """
                {
                  "email": "manual@test.com",
                  "subject": "Тест API",
                  "message": "Привет! Это письмо отправлено через REST API"
                }
                """;

        mockMvc.perform(post("/notifications/email")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Письмо отправлено на manual@test.com"))
                .andExpect(jsonPath("$._links.self.href")
                        .value(containsString("/notifications/email")));

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {

        String requestJson = """
                {
                  "email": "not-email",
                  "subject": "Тест",
                  "message": "Сообщение"
                }
                """;

        mockMvc.perform(post("/notifications/email")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_shouldReturnBadRequest_whenEmailIsEmpty() throws Exception {

        String requestJson = """
                {
                  "email": "",
                  "subject": "Тест",
                  "message": "Сообщение"
                }
                """;

        mockMvc.perform(post("/notifications/email")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_shouldReturnBadRequest_whenSubjectIsEmpty() throws Exception {

        String requestJson = """
                {
                  "email": "test@mail.com",
                  "subject": "",
                  "message": "Сообщение"
                }
                """;

        mockMvc.perform(post("/notifications/email")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_shouldReturnBadRequest_whenMessageIsEmpty() throws Exception {

        String requestJson = """
                {
                  "email": "test@mail.com",
                  "subject": "Тест",
                  "message": ""
                }
                """;

        mockMvc.perform(post("/notifications/email")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}