package com.contextoia.documentingestion.application.port.in;

import com.contextoia.documentingestion.application.dto.DocumentDetailsResponse;

import java.util.UUID;

public interface GetDocumentUseCase {
    DocumentDetailsResponse execute(UUID documentId, UUID userId);
}

