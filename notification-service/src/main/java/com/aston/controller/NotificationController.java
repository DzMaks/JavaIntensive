package com.aston.controller;

import com.aston.dto.EmailRequest;
import com.aston.dto.EmailResponse;
import com.aston.hateoas.EmailModelAssembler;
import com.aston.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Notifications", description = "Отправка email уведомлений")
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final EmailService emailService;
    private final EmailModelAssembler assembler;

    public NotificationController(EmailService emailService,
                                  EmailModelAssembler assembler) {
        this.emailService = emailService;
        this.assembler = assembler;
    }

    @Operation(summary = "Отправить email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email отправлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PostMapping("/email")
    public ResponseEntity<EntityModel<EmailResponse>> sendEmail(
            @RequestBody @Valid EmailRequest request) {

        emailService.sendEmail(
                request.getEmail(),
                request.getSubject(),
                request.getMessage()
        );

        EmailResponse response =
                new EmailResponse("Письмо отправлено на " + request.getEmail());

        EntityModel<EmailResponse> model = assembler.toModel(response);

        return ResponseEntity.ok(model);
    }
}