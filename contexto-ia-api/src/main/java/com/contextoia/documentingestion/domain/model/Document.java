package com.contextoia.documentingestion.domain.model;


import com.contextoia.documentingestion.domain.enums.DocumentStatus;
import com.pgvector.PGvector;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_documents")
public class Document {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "file_name", nullable = false, length = 400)
    private String fileName;

    @Column(name = "original_file_name", nullable = false, length = 1024)
    private String originalFileName;

    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content-type", nullable = false)
    private String contentType;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentStatus status = DocumentStatus.PENDING;

    @Column(name = "embedding", columnDefinition = "vector(768)")
    private PGvector embedding;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "extracted_text", columnDefinition = "TEXT")
    private String extractedText;

    public Document() {}

    @PrePersist
    @PreUpdate
    private void validate() {
        validateFileName();
        validateFileExtension();
        validateContentType();
    }

    private void validateFileName() {
        if (originalFileName != null && !isSafeFileName(originalFileName)) {
            throw new IllegalArgumentException("Nome do arquivo contém caracteres inválidos");
        }
    }

    private void validateFileExtension() {
        if (originalFileName != null && !originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Apenas arquivos .pdf são aceitos");
        }
    }

    private void validateContentType() {
        if (contentType != null && !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Content-Type deve ser application/pdf");
        }
    }

    private boolean isSafeFileName(String fileName) {
        return fileName.matches("^[a-zA-Z0-9._\\-\\s]+$");
    }

    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public String getStorageKey() { return storageKey; }
    public Long getFileSize() { return fileSize; }
    public String getContentType() { return contentType; }
    public String getExtractedText() { return extractedText; }
    public PGvector getEmbedding() { return embedding; }
    public UUID getUserId() { return userId; }
    public DocumentStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }


    public void assignToUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID não pode ser nulo");
        }
        this.userId = userId;
    }

    public void startProcessing() {
        if (this.status != DocumentStatus.PENDING) {
            throw new IllegalStateException("Apenas documentos pendentes podem iniciar processamento");
        }
        this.status = DocumentStatus.PROCESSING;
    }

    public void completeProcessing() {
        if (this.status != DocumentStatus.PROCESSING) {
            throw new IllegalStateException("Apenas documentos em processamento podem ser completados");
        }
        this.status = DocumentStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    public void failProcessing(String errorMessage) {
        if (this.status == DocumentStatus.COMPLETED) {
            throw new IllegalStateException("Documentos completados não podem falhar");
        }
        this.status = DocumentStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isPending() { return this.status == DocumentStatus.PENDING; }
    public boolean isProcessing() { return this.status == DocumentStatus.PROCESSING; }
    public boolean isCompleted() { return this.status == DocumentStatus.COMPLETED; }
    public boolean isFailed() { return this.status == DocumentStatus.FAILED; }
    public boolean canBeProcessed() { return isPending(); }
}
