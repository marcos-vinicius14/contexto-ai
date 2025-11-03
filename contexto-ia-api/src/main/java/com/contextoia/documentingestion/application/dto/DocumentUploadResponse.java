package com.contextoia.documentingestion.application.dto;

import com.contextoia.documentingestion.domain.enums.DocumentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentUploadResponse(
        UUID id,
        String filename,
        Long fileSize,
        DocumentStatus status,
        LocalDateTime createdAt,
        String message
) {
}
