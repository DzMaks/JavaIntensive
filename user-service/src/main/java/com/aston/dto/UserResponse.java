package com.aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными пользователя")
public class UserResponse {

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Alex")
    private String name;

    @Schema(description = "Email пользователя", example = "alex@mail.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "25")
    private int age;

    public UserResponse() {}

    public UserResponse(Long id, String name, String email, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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