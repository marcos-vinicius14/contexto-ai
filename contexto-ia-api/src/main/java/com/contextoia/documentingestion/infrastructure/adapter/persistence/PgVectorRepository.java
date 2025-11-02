package com.contextoia.documentingestion.infrastructure.adapter.persistence;

import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface PgVectorRepository extends Repository<Document, UUID> {

    @Query(value = """
        SELECT d.*,
               1 - (d.embedding <=> CAST(:embedding AS vector)) AS similarity
        FROM tb_documents d
        WHERE d.embedding IS NOT NULL
          AND d.user_id = :userId
        ORDER BY d.embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<Document> findSimilarDocuments(
            @Param("embedding") String embedding,
            @Param("userId") UUID userId,
            @Param("limit") int limit
    );
}
