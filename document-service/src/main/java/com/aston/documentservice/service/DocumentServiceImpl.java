package com.aston.documentservice.service;

import com.aston.documentservice.client.UserClient;
import com.aston.documentservice.dto.DocumentRequest;
import com.aston.documentservice.dto.DocumentResponse;
import com.aston.documentservice.entity.Document;
import com.aston.documentservice.mapper.DocumentMapper;
import com.aston.documentservice.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository repository;
    private final DocumentMapper mapper;
    private final UserClient userClient;


    public DocumentServiceImpl(DocumentRepository repository,
                               DocumentMapper mapper,
                               UserClient userClient) {

        this.repository = repository;
        this.mapper = mapper;
        this.userClient = userClient;
    }


    @Override
    public DocumentResponse create(DocumentRequest request) {

        // Проверяем наличие пользователя через user-service
        userClient.getUser(request.getUserId());


        Document document = mapper.toEntity(request);


        Document saved = repository.save(document);


        return mapper.toResponse(saved);
    }


    @Override
    public DocumentResponse getById(Long id) {

        Document document = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return mapper.toResponse(document);
    }


    @Override
    public List<DocumentResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    @Override
    public List<DocumentResponse> getByUserId(Long userId) {

        // Проверяем существование пользователя
        userClient.getUser(userId);


        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    @Override
    public void delete(Long id) {

        repository.deleteById(id);
    }
}