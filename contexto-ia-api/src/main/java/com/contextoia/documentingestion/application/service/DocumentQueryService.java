package com.contextoia.documentingestion.application.service;

import com.contextoia.documentingestion.application.dto.DocumentDetailsResponse;
import com.contextoia.documentingestion.application.dto.SearchSimilarRequest;
import com.contextoia.documentingestion.application.dto.SimilarDocumentResponse;
import com.contextoia.documentingestion.application.port.in.GetDocumentUseCase;
import com.contextoia.documentingestion.application.port.in.ListUserDocumentsUseCase;
import com.contextoia.documentingestion.application.port.in.SearchSimilarDocumentsUseCase;
import com.contextoia.documentingestion.application.port.out.DocumentRepositoryPort;
import com.contextoia.documentingestion.application.port.out.DocumentVectorStoragePort;
import com.contextoia.documentingestion.application.port.out.EmbeddingPort;
import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentQueryService implements
        GetDocumentUseCase,
        ListUserDocumentsUseCase,
        SearchSimilarDocumentsUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final DocumentVectorStoragePort vectorStorage;
    private final EmbeddingPort embeddingPort;

    public DocumentQueryService(
            DocumentRepositoryPort documentRepository,
            DocumentVectorStoragePort vectorStorage,
            EmbeddingPort embeddingPort) {
        this.documentRepository = documentRepository;
        this.vectorStorage = vectorStorage;
        this.embeddingPort = embeddingPort;
    }

    @Override
    public DocumentDetailsResponse execute(UUID documentId, UUID userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        if (!document.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }

        return mapToDetailsResponse(document);
    }

    @Override
    public List<DocumentDetailsResponse> execute(UUID userId) {
        List<Document> documents = documentRepository.findByUserId(userId);
        return documents.stream()
                .map(this::mapToDetailsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SimilarDocumentResponse> execute(SearchSimilarRequest request, UUID userId) {
        float[] queryEmbedding = embeddingPort.generateEmbedding(request.query());
        List<Document> similarDocs = vectorStorage.findSimilarDocuments(
                queryEmbedding,
                userId,
                request.limit()
        );

        return similarDocs.stream()
                .map(this::mapToSimilarResponse)
                .collect(Collectors.toList());
    }

    private DocumentDetailsResponse mapToDetailsResponse(Document document) {
        return new DocumentDetailsResponse(
                document.getId(),
                document.getFileName(),
                document.getOriginalFileName(),
                document.getFileSize(),
                document.getStatus(),
                document.getErrorMessage(),
                document.getCreatedAt(),
                document.getProcessedAt()
        );
    }

    private SimilarDocumentResponse mapToSimilarResponse(Document document) {
        return new SimilarDocumentResponse(
                document.getId(),
                document.getOriginalFileName(),
                truncateText(document.getExtractedText(), 200),
                0.95f // Implementar cálculo real de similaridade
        );
    }

    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}