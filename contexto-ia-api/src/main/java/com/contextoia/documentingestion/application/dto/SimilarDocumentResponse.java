package com.contextoia.documentingestion.application.dto;

import java.util.UUID;

public record SimilarDocumentResponse(
        UUID id,
        String fileName,
        String extractedText,
        float similarity
) {

}

