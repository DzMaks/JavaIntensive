package com.aston.documentservice.service;

import com.aston.documentservice.dto.DocumentRequest;
import com.aston.documentservice.dto.DocumentResponse;

import java.util.List;

public interface DocumentService {

    DocumentResponse create(DocumentRequest request);

    DocumentResponse getById(Long id);

    List<DocumentResponse> getAll();

    List<DocumentResponse> getByUserId(Long userId);

    void delete(Long id);
}