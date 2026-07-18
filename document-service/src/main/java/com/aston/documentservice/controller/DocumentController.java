package com.aston.documentservice.controller;

import com.aston.documentservice.dto.DocumentRequest;
import com.aston.documentservice.dto.DocumentResponse;
import com.aston.documentservice.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> create(@RequestBody DocumentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public List<DocumentResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DocumentResponse getById(@PathVariable("id") Long id) {

        return service.getById(id);
    }


    @GetMapping("/user/{userId}")
    public List<DocumentResponse> getByUserId(
            @PathVariable("userId") Long userId) {

        return service.getByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}