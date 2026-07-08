package com.aston.hateoas;

import com.aston.controller.UserController;
import com.aston.dto.UserResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler
        implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse response) {

        return EntityModel.of(
                response,

                linkTo(methodOn(UserController.class)
                        .getById(response.getId()))
                        .withSelfRel(),

                linkTo(methodOn(UserController.class)
                        .getAll())
                        .withRel("collection"),

                linkTo(methodOn(UserController.class)
                        .update(response.getId(), null))
                        .withRel("update"),

                linkTo(methodOn(UserController.class)
                        .delete(response.getId()))
                        .withRel("delete")
        );
    }
}