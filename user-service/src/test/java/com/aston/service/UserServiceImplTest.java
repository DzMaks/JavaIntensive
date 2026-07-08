package com.aston.service;

import com.aston.common.event.UserEvent;
import com.aston.dto.UserRequest;
import com.aston.dto.UserResponse;
import com.aston.entity.User;
import com.aston.exception.BadRequestException;
import com.aston.exception.UserNotFoundException;
import com.aston.kafka.producer.UserEventProducer;
import com.aston.mapper.UserMapper;
import com.aston.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserEventProducer producer;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void create_ShouldCreateUser() {

        UserRequest request = new UserRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");
        request.setAge(25);

        User user = new User("Ivan", "ivan@mail.com", 25);
        User savedUser = new User("Ivan", "ivan@mail.com", 25);
        savedUser.setId(1L);

        UserResponse response = new UserResponse(1L, "Ivan", "ivan@mail.com", 25);

        when(repository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(mapper.toEntity(request)).thenReturn(user);
        when(repository.save(user)).thenReturn(savedUser);
        when(mapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = service.create(request);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());

        verify(repository).save(user);
        verify(producer).send(any(UserEvent.class));
    }

    @Test
    void create_ShouldThrowException_WhenAgeIsNegative() {

        UserRequest request = new UserRequest();
        request.setAge(-5);

        assertThrows(BadRequestException.class,
                () -> service.create(request));

        verify(repository, never()).save(any());
        verify(producer, never()).send(any());
    }

    @Test
    void create_ShouldThrowException_WhenEmailExists() {

        UserRequest request = new UserRequest();
        request.setEmail("test@mail.com");
        request.setAge(20);

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class,
                () -> service.create(request));

        verify(repository, never()).save(any());
        verify(producer, never()).send(any());
    }

    @Test
    void getById_ShouldReturnUser() {

        User user = new User("Ivan", "mail@mail.com", 25);
        user.setId(1L);

        UserResponse response = new UserResponse(1L, "Ivan", "mail@mail.com", 25);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(response);

        UserResponse result = service.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());
    }

    @Test
    void getById_ShouldThrowException_WhenUserNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    void getAll_ShouldReturnUsers() {

        User user = new User("Ivan", "mail@mail.com", 25);

        when(repository.findAll()).thenReturn(List.of(user));
        when(mapper.toResponse(user))
                .thenReturn(new UserResponse(1L, "Ivan", "mail@mail.com", 25));

        List<UserResponse> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Ivan", result.get(0).getName());
    }

    @Test
    void getAll_ShouldReturnEmptyList() {

        when(repository.findAll()).thenReturn(List.of());

        List<UserResponse> result = service.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void update_ShouldUpdateUser() {

        UserRequest request = new UserRequest();
        request.setName("New Name");
        request.setEmail("mail@mail.com");
        request.setAge(30);

        User user = new User("Old", "mail@mail.com", 25);
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail("mail@mail.com"))
                .thenReturn(Optional.of(user));

        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.toResponse(any(User.class)))
                .thenReturn(new UserResponse(1L, "New Name", "mail@mail.com", 30));

        UserResponse result = service.update(1L, request);

        assertEquals("New Name", result.getName());

        verify(repository).save(user);
    }

    @Test
    void update_ShouldThrowException_WhenUserNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.update(1L, new UserRequest()));
    }

    @Test
    void update_ShouldThrowException_WhenAgeIsNegative() {

        UserRequest request = new UserRequest();
        request.setAge(-1);

        User user = new User();
        user.setEmail("mail@mail.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> service.update(1L, request));
    }

    @Test
    void update_ShouldThrowException_WhenEmailExists() {

        UserRequest request = new UserRequest();
        request.setEmail("new@mail.com");
        request.setAge(20);

        User user = new User("Old", "old@mail.com", 20);
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail("new@mail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class,
                () -> service.update(1L, request));
    }

    @Test
    void update_ShouldAllowSameEmail() {

        UserRequest request = new UserRequest();
        request.setEmail("same@mail.com");
        request.setAge(20);

        User user = new User("Old", "same@mail.com", 20);
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail("same@mail.com"))
                .thenReturn(Optional.of(user));

        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.toResponse(any(User.class)))
                .thenReturn(new UserResponse(1L, "Old", "same@mail.com", 20));

        UserResponse result = service.update(1L, request);

        assertEquals("same@mail.com", result.getEmail());

        verify(repository).save(user);
    }

    @Test
    void delete_ShouldDeleteUser() {

        User user = new User("Ivan", "mail@mail.com", 25);
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository).deleteById(1L);
        verify(producer).send(any(UserEvent.class));
    }

    @Test
    void delete_ShouldThrowException_WhenUserNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.delete(1L));

        verify(repository, never()).deleteById(any());
        verify(producer, never()).send(any());
    }

}