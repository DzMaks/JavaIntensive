package com.aston.mapper;

import com.aston.dto.UserRequest;
import com.aston.dto.UserResponse;
import com.aston.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_ShouldMapCorrectly() {

        UserRequest request = new UserRequest();
        request.setName("Ivan");
        request.setEmail("ivan@mail.com");
        request.setAge(25);

        User user = mapper.toEntity(request);

        assertNotNull(user);
        assertEquals("Ivan", user.getName());
        assertEquals("ivan@mail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void toResponse_ShouldMapCorrectly() {

        User user = new User("Ivan", "ivan@mail.com", 25);
        user.setId(1L);

        UserResponse response = mapper.toResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Ivan", response.getName());
        assertEquals("ivan@mail.com", response.getEmail());
        assertEquals(25, response.getAge());
    }

    @Test
    void toEntity_ShouldHandleNullFields() {

        UserRequest request = new UserRequest();
        request.setName(null);
        request.setEmail(null);
        request.setAge(0);

        User user = mapper.toEntity(request);

        assertNotNull(user);
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertEquals(0, user.getAge());
    }
}