package com.contextoia.documentingestion.application.dto;

import com.contextoia.documentingestion.domain.enums.DocumentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentDetailsResponse(
        UUID id,
        String fileName,
        String originalFileName,
        Long fileSize,
        DocumentStatus status,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime processedAt
) {
}
