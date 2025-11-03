package com.contextoia.documentingestion.infrastructure.adapter.persistence;

import com.contextoia.documentingestion.application.port.out.DocumentRepositoryPort;
import com.contextoia.documentingestion.domain.enums.DocumentStatus;
import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter class that implements the {@link DocumentRepositoryPort} interface,
 * providing an integration layer between the domain model and the JPA persistence mechanism.
 * It delegates the operations to the {@link JpaDocumentRepository}.
 *
 * This class is marked as a Spring {@code @Repository}, making it a component
 * responsible for data access and enabling dependency injection.
 */
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
