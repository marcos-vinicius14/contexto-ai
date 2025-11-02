package com.contextoia.documentingestion.infrastructure.adapter.persistence;

import com.contextoia.documentingestion.application.port.out.DocumentVectorStoragePort;
import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public class PgVectorStorageAdapter implements DocumentVectorStoragePort {

    private final PgVectorRepository pgVectorRepository;

    public PgVectorStorageAdapter(PgVectorRepository pgVectorRepository) {
        this.pgVectorRepository = pgVectorRepository;
    }

    @Override
    public List<Document> findSimilarDocuments(float[] embedding, UUID userId, int limit) {
        String embeddingStr = formatFloatArrayAsVectorString(embedding);
        return pgVectorRepository.findSimilarDocuments(embeddingStr, userId, limit);
    }

    private static final String VECTOR_DELIMITER = ",";

    private String formatFloatArrayAsVectorString(float[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(VECTOR_DELIMITER);
            sb.append(array[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}