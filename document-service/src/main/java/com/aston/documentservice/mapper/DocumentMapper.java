package com.aston.documentservice.mapper;

import com.aston.documentservice.dto.DocumentRequest;
import com.aston.documentservice.dto.DocumentResponse;
import com.aston.documentservice.entity.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DocumentMapper {

    public Document toEntity(DocumentRequest request) {

        Document document = new Document();

        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setUserId(request.getUserId());
        document.setCreatedAt(LocalDateTime.now());

        return document;
    }

    public DocumentResponse toResponse(Document document) {

        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getContent(),
                document.getUserId(),
                document.getCreatedAt()
        );
    }
}