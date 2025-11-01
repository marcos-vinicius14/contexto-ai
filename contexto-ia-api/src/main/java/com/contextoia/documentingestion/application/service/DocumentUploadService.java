package com.contextoia.documentingestion.application.service;

import com.contextoia.documentingestion.application.dto.DocumentUploadResponse;
import com.contextoia.documentingestion.application.dto.ProcessDocumentMessage;
import com.contextoia.documentingestion.application.port.in.UploadDocumentUseCase;
import com.contextoia.documentingestion.application.port.out.DocumentMessagePublisher;
import com.contextoia.documentingestion.application.port.out.DocumentRepositoryPort;
import com.contextoia.documentingestion.application.port.out.StoragePort;
import com.contextoia.documentingestion.domain.model.Document;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class UploadDocumentService implements UploadDocumentUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final StoragePort storagePort;
    private final DocumentMessagePublisher messagePublisher;

    public UploadDocumentService(
            DocumentRepositoryPort documentRepository,
            StoragePort storagePort,
            DocumentMessagePublisher messagePublisher) {
        this.documentRepository = documentRepository;
        this.storagePort = storagePort;
        this.messagePublisher = messagePublisher;
    }

    @Override
    @Transactional
    public DocumentUploadResponse execute(MultipartFile file, UUID userId) throws IOException {
        validateFile(file);

        Document document = createDocument(file, userId);

        String storageKey = storagePort.store(file, document.getFileName());
        document.setStorageKey(storageKey);

        Document savedDocument = documentRepository.save(document);

        ProcessDocumentMessage message = new ProcessDocumentMessage(
                savedDocument.getId(),
                savedDocument.getStorageKey()
        );
        messagePublisher.publishProcessingMessage(message);

        return new DocumentUploadResponse(
                savedDocument.getId(),
                savedDocument.getOriginalFileName(),
                savedDocument.getFileSize(),
                savedDocument.getStatus(),
                savedDocument.getCreatedAt(),
                "Documento enviado para processamento"
        );
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode estar vazio");
        }

        if (!isPDF(file)) {
            throw new IllegalArgumentException("Apenas arquivos PDF são permitidos");
        }

        if (file.getSize() > 52428800) { // 50MB
            throw new IllegalArgumentException("Arquivo excede o tamanho máximo de 50MB");
        }
    }

    private boolean isPDF(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        return (contentType != null && contentType.equals("application/pdf")) &&
                (originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf"));
    }

    private Document createDocument(MultipartFile file, UUID userId) {
        Document document = new Document();
        document.setOriginalFileName(sanitizeFileName(file.getOriginalFilename()));
        document.setFileName(storagePort.generateFileName(file.getOriginalFilename()));
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.assignToUser(userId);
        return document;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unnamed.pdf";
        return fileName.replaceAll("[^a-zA-Z0-9._\\-]", "_");
    }
}