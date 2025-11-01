package com.contextoia.documentingestion.infrastructure.adapter.persistence;

import com.contextoia.documentingestion.domain.enums.DocumentStatus;
import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface JpaDocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByUserId(UUID userId);
    List<Document> findByUserIdAndStatus(UUID userId, DocumentStatus status);
}
