package com.contextoia.documentingestion.application.port.in;

import com.contextoia.documentingestion.application.dto.DocumentDetailsResponse;

import java.util.List;
import java.util.UUID;

public interface ListUserDocumentsUseCase {
    List<DocumentDetailsResponse> execute(UUID userId);
}
