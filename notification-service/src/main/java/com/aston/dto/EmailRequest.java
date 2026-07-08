package com.aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на отправку email")
public class EmailRequest {

    @Schema(description = "Email получателя", example = "test@mail.com")
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный email")
    private String email;

    @Schema(description = "Тема письма", example = "Hello")
    @NotBlank(message = "Тема письма не должна быть пустой")
    private String subject;

    @Schema(description = "Текст письма", example = "Привет!")
    @NotBlank(message = "Текст письма не должен быть пустым")
    private String message;

    public EmailRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}