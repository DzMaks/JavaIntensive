package com.aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ об отправке email")
public class EmailResponse {

    @Schema(description = "Сообщение результата")
    private String message;

    public EmailResponse() {
    }

    public EmailResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}