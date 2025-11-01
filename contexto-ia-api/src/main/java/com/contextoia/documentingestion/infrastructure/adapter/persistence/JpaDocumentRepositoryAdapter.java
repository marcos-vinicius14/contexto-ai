package com.contextoia.documentingestion.infrastructure.adapter.persistence;

import com.contextoia.documentingestion.application.port.out.DocumentRepositoryPort;
import com.contextoia.documentingestion.domain.enums.DocumentStatus;
import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final JpaDocumentRepository jpaRepository;

    public JpaDocumentRepositoryAdapter(JpaDocumentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Document save(Document document) {
        return jpaRepository.save(document);
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Document> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public List<Document> findByUserIdAndStatus(UUID userId, DocumentStatus status) {
        return jpaRepository.findByUserIdAndStatus(userId, status);
    }
}
