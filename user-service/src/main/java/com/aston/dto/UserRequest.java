package com.aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на создание или обновление пользователя")
public class UserRequest {

    @Schema(description = "Имя пользователя", example = "Alex")
    private String name;
    @Schema(description = "Email пользователя", example = "alex@mail.com")
    private String email;
    @Schema(description = "Возраст пользователя", example = "25")
    private int age;

    public UserRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
