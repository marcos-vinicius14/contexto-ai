package com.contextoia.documentingestion.application.port.out;

import com.contextoia.documentingestion.domain.model.Document;

import java.util.List;

public interface DocumentVectorStoragePort {
    List<Document> findSimilarDocuments(float[] embedding, UUID userId, int limit);
}