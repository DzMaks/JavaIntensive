package com.aston.hateoas;

import com.aston.controller.NotificationController;
import com.aston.dto.EmailResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EmailModelAssembler
        implements RepresentationModelAssembler<EmailResponse, EntityModel<EmailResponse>> {

    @Override
    public EntityModel<EmailResponse> toModel(EmailResponse response) {

        return EntityModel.of(
                response,

                linkTo(methodOn(NotificationController.class)
                        .sendEmail(null))
                        .withSelfRel()
        );
    }
}