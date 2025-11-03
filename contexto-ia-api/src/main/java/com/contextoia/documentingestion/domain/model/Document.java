package com.contextoia.documentingestion.domain.model;


import com.contextoia.documentingestion.domain.enums.DocumentStatus;
import com.pgvector.PGvector;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Document entity, mapped to the table "tb_documents".
 * Used for storing metadata of documents processed by the application.
 */
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

    protected Document() {}

    private Document(Builder builder) {
        this.fileName = builder.fileName;
        this.originalFileName = builder.originalFileName;
        this.storageKey = builder.storageKey;
        this.fileSize = builder.fileSize;
        this.contentType = builder.contentType;
        this.extractedText = builder.extractedText;
        this.embedding = builder.embedding;
        this.userId = builder.userId;
        this.status = builder.status != null ? builder.status : DocumentStatus.PENDING;
        this.errorMessage = builder.errorMessage;
    }

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


    public Document withStorageKey(String storageKey) {
        return Document.builder()
                .from(this)
                .storageKey(storageKey)
                .build();
    }

    public Document withExtractedText(String extractedText) {
        if (!isProcessing()) {
            throw new IllegalStateException("Texto só pode ser atualizado durante processamento");
        }
        return Document.builder()
                .from(this)
                .extractedText(extractedText)
                .build();
    }

    public Document withEmbedding(PGvector embedding) {
        if (!isProcessing()) {
            throw new IllegalStateException("Embedding só pode ser atualizado durante processamento");
        }
        return Document.builder()
                .from(this)
                .embedding(embedding)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fileName;
        private String originalFileName;
        private String storageKey;
        private Long fileSize;
        private String contentType;
        private String extractedText;
        private PGvector embedding;
        private UUID userId;
        private DocumentStatus status;
        private String errorMessage;

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder originalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
            return this;
        }

        public Builder storageKey(String storageKey) {
            this.storageKey = storageKey;
            return this;
        }

        public Builder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder extractedText(String extractedText) {
            this.extractedText = extractedText;
            return this;
        }

        public Builder embedding(PGvector embedding) {
            this.embedding = embedding;
            return this;
        }

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder status(DocumentStatus status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        // Método para copiar de um documento existente
        public Builder from(Document document) {
            this.fileName = document.fileName;
            this.originalFileName = document.originalFileName;
            this.storageKey = document.storageKey;
            this.fileSize = document.fileSize;
            this.contentType = document.contentType;
            this.extractedText = document.extractedText;
            this.embedding = document.embedding;
            this.userId = document.userId;
            this.status = document.status;
            this.errorMessage = document.errorMessage;
            return this;
        }

        public Document build() {
            return new Document(this);
        }
    }
}
