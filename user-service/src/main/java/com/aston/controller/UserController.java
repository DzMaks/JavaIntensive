package com.aston.controller;

import com.aston.dto.UserRequest;
import com.aston.dto.UserResponse;
import com.aston.hateoas.UserModelAssembler;
import com.aston.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Users", description = "CRUD операции с пользователем")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    private final UserModelAssembler assembler;

    public UserController(UserService service,
                          UserModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAll() {

        List<EntityModel<UserResponse>> users = service.getAll()
                .stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel(),
                linkTo(methodOn(UserController.class).create(null)).withRel("create")
        );
    }

    @GetMapping("/internal/{id}")
    public UserResponse getInternal(
            @PathVariable("id") Long id
    ) {
        return service.getById(id);
    }

    @Operation(summary = "Получить пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getById(@PathVariable("id") Long id) {
        return assembler.toModel(service.getById(id));
    }

    @Operation(summary = "Создать пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> create(@RequestBody UserRequest request) {

        UserResponse response = service.create(request);

        EntityModel<UserResponse> model = assembler.toModel(response);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class)
                        .getById(response.getId()))
                        .toUri())
                .body(model);
    }

    @Operation(summary = "Обновить пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PutMapping("/{id}")
    public EntityModel<UserResponse> update(@PathVariable("id") Long id,
                                            @RequestBody UserRequest request) {
        return assembler.toModel(service.update(id, request));
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}