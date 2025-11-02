package com.contextoia.documentingestion.application.port.in;

import com.contextoia.documentingestion.application.dto.DocumentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UploadDocumentUseCase {
    DocumentUploadResponse execute(MultipartFile file, UUID userId);
}