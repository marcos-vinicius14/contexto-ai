package com.contextoia.documentingestion.application.dto;

import java.util.UUID;

public record ProcessDocumentMessage(
        UUID documentId,
        String storageKey
) {
}
