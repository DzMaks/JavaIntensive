package com.aston.controller;

import com.aston.dto.UserRequest;
import com.aston.dto.UserResponse;
import com.aston.exception.BadRequestException;
import com.aston.exception.UserNotFoundException;
import com.aston.hateoas.UserModelAssembler;
import com.aston.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserModelAssembler assembler;

    @Test
    void getAllUsers() throws Exception {

        UserResponse response =
                new UserResponse(1L, "Ivan", "ivan@mail.com", 25);

        when(userService.getAll()).thenReturn(List.of(response));

        EntityModel<UserResponse> model = EntityModel.of(response);
        when(assembler.toModel(response)).thenReturn(model);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.userResponseList[0].name").value("Ivan"))
                .andExpect(jsonPath("$._embedded.userResponseList[0].email").value("ivan@mail.com"))
                .andExpect(jsonPath("$._embedded.userResponseList[0].age").value(25))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.create.href").exists());
    }

    @Test
    void getUserById() throws Exception {

        UserResponse response =
                new UserResponse(1L, "Ivan", "ivan@mail.com", 25);

        when(userService.getById(1L)).thenReturn(response);

        EntityModel<UserResponse> model = EntityModel.of(response);
        when(assembler.toModel(response)).thenReturn(model);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@mail.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void createUser() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");
        request.setAge(25);

        UserResponse response =
                new UserResponse(1L, "Ivan", "ivan@mail.com", 25);

        when(userService.create(any(UserRequest.class))).thenReturn(response);

        EntityModel<UserResponse> model = EntityModel.of(response);
        when(assembler.toModel(response)).thenReturn(model);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"));
    }

    @Test
    void updateUser() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("Ivan updated");
        request.setEmail("ivan@mail.com");
        request.setAge(30);

        UserResponse response =
                new UserResponse(1L, "Ivan updated", "ivan@mail.com", 30);

        when(userService.update(eq(1L), any(UserRequest.class)))
                .thenReturn(response);

        EntityModel<UserResponse> model = EntityModel.of(response);
        when(assembler.toModel(response)).thenReturn(model);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan updated"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void deleteUser() throws Exception {

        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void getUserById_NotFound() throws Exception {

        when(userService.getById(100L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void createUser_BadRequest() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");
        request.setAge(-10);

        when(userService.create(any(UserRequest.class)))
                .thenThrow(new BadRequestException("Возраст не может быть отрицательным"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Возраст не может быть отрицательным"));
    }

    @Test
    void updateUser_NotFound() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");
        request.setAge(30);

        when(userService.update(eq(100L), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void deleteUser_NotFound() throws Exception {

        doThrow(new UserNotFoundException("User not found"))
                .when(userService)
                .delete(100L);

        mockMvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void getUser_InternalServerError() throws Exception {

        when(userService.getById(1L))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    void updateUser_BadRequest() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");
        request.setAge(-5);

        when(userService.update(eq(1L), any(UserRequest.class)))
                .thenThrow(new BadRequestException("Возраст не может быть отрицательным"));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Возраст не может быть отрицательным"));
    }

}