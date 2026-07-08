package com.aston.hateoas;

import com.aston.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import static org.junit.jupiter.api.Assertions.*;

class UserModelAssemblerTest {

    private final UserModelAssembler assembler = new UserModelAssembler();

    @Test
    void toModel_ShouldCreateLinksCorrectly() {

        UserResponse response = new UserResponse(
                1L,
                "Ivan",
                "ivan@mail.com",
                25
        );

        EntityModel<UserResponse> model = assembler.toModel(response);

        assertNotNull(model);

        assertTrue(model.getLink("self").isPresent());
        assertEquals("/users/1",
                model.getRequiredLink("self").getHref());

        assertTrue(model.getLink("collection").isPresent());

        assertTrue(model.getLink("update").isPresent());

        assertTrue(model.getLink("delete").isPresent());
    }

    @Test
    void toModel_ShouldContainAllLinks() {

        UserResponse response = new UserResponse(2L, "Alex", "alex@mail.com", 30);

        EntityModel<UserResponse> model = assembler.toModel(response);

        assertEquals(4, model.getLinks().toList().size());
    }
}