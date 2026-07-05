package com.aston.controller;

import com.aston.dto.EmailRequest;
import com.aston.dto.EmailResponse;
import com.aston.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody @Valid EmailRequest request) {
        emailService.sendEmail(
                request.getEmail(),
                request.getSubject(),
                request.getMessage()
        );

        EmailResponse response =
                new EmailResponse("Письмо отправлено на " + request.getEmail());

        return ResponseEntity.ok(response);
    }
}