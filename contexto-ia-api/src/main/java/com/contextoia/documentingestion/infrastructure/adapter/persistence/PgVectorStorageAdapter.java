package com.contextoia.documentingestion.infrastructure.adapter.persistence;

import com.contextoia.documentingestion.application.port.out.DocumentVectorStoragePort;
import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


/**
 * Adapter class for handling operations related to document vector storage
 * using a PostgreSQL database with vector similarity search capabilities.
 *
 * This class implements the {@link DocumentVectorStoragePort} interface
 * to provide functionality for querying documents based on vector similarity.
 * It relies on the {@link PgVectorRepository} to interact with the database.
 */
@Repository
public class PgVectorStorageAdapter implements DocumentVectorStoragePort {

    private final PgVectorRepository pgVectorRepository;

    public PgVectorStorageAdapter(PgVectorRepository pgVectorRepository) {
        this.pgVectorRepository = pgVectorRepository;
    }

    /**
     * Finds documents similar to the provided embedding vector for a specific user.
     * The similarity is determined based on the vector distance, and results are limited
     * to the specified maximum count.
     *
     * @param embedding a float array representing the embedding vector to compare against
     * @param userId the unique identifier of the user whose documents are to be searched
     * @param limit the maximum number of similar documents to retrieve
     * @return a list of documents that are most similar to the given embedding vector, sorted by similarity
     */
    @Override
    public List<Document> findSimilarDocuments(Float[] embedding, UUID userId, int limit) {
        String embeddingStr = formatFloatArrayAsVectorString(embedding);
        return pgVectorRepository.findSimilarDocuments(embeddingStr, userId, limit);
    }

    private static final String VECTOR_DELIMITER = ",";

    /**
     * Converts an array of floats into a formatted string representation of a vector.
     * Each value in the array is separated by a delimiter and enclosed in brackets.
     *
     * @param array a float array to be converted into a vector string format
     * @return a string representation of the array in vector format, with elements separated by a delimiter
     */
    private String formatFloatArrayAsVectorString(Float[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(VECTOR_DELIMITER);
            sb.append(array[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}