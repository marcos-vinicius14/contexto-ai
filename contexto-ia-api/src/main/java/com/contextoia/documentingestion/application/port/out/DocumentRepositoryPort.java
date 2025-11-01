package com.contextoia.documentingestion.application.port.out;

import com.contextoia.documentingestion.domain.enums.DocumentStatus;
import com.contextoia.documentingestion.domain.model.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepositoryPort {
    Document save(Document document);
    Optional<Document> findById(UUID id);
    List<Document> findByUserId(UUID userId);
    List<Document> findByUserIdAndStatus(UUID userId, DocumentStatus status);
}