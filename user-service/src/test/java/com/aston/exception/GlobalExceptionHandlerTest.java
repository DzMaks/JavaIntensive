package com.aston.exception;

import com.aston.controller.UserController;
import com.aston.dto.UserRequest;
import com.aston.hateoas.UserModelAssembler;
import com.aston.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserModelAssembler assembler;

    @Test
    void shouldReturn404_WhenUserNotFound() throws Exception {

        when(userService.getById(1L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void shouldReturn400_WhenBadRequest() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");
        request.setAge(25);

        when(userService.create(any(UserRequest.class)))
                .thenThrow(new BadRequestException("Invalid data"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    @Test
    void shouldReturn500_WhenUnexpectedError() throws Exception {

        when(userService.getById(eq(1L)))
                .thenThrow(new RuntimeException("DB crash"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

}