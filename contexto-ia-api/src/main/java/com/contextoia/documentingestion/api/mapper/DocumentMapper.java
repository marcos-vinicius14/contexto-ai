package com.contextoia.documentingestion.api.mapper;
import com.contextoia.documentingestion.application.dto.DocumentDetailsResponse;
import com.contextoia.documentingestion.application.dto.DocumentUploadResponse;
import com.contextoia.documentingestion.domain.model.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentMapper {

    public DocumentUploadResponse toUploadResponse(Document document, String message) {
        return new DocumentUploadResponse(
                document.getId(),
                document.getOriginalFileName(),
                document.getFileSize(),
                document.getStatus(),
                document.getCreatedAt(),
                message
        );
    }

    public DocumentDetailsResponse toDetailsResponse(Document document) {
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

    public List<DocumentDetailsResponse> toDetailsResponseList(List<Document> documents) {
        return documents.stream()
                .map(this::toDetailsResponse)
                .collect(Collectors.toList());
    }
}