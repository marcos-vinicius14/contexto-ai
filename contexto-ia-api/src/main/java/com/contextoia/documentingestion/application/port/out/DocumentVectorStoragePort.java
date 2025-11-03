package com.contextoia.documentingestion.application.port.out;

import com.contextoia.documentingestion.domain.model.Document;

import java.util.List;
import java.util.UUID;

public interface DocumentVectorStoragePort {
    List<Document> findSimilarDocuments(Float[] embedding, UUID userId, int limit);
}