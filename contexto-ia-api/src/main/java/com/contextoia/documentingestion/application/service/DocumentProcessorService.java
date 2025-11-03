package com.contextoia.documentingestion.application.service;

import com.contextoia.common.exceptions.ResourceNotFoundException;
import com.contextoia.documentingestion.application.port.out.DocumentRepositoryPort;
import com.contextoia.documentingestion.application.port.out.EmbeddingPort;
import com.contextoia.documentingestion.application.port.out.PdfProcessorPort;
import com.contextoia.documentingestion.application.port.out.StoragePort;
import com.contextoia.documentingestion.domain.model.Document;
import com.pgvector.PGvector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Service responsible for handling the processing of documents. This includes
 * extracting text from documents, generating embeddings, and managing the
 * state of the document during the processing pipeline.
 *
 * The class leverages various ports/interfaces for specific functionalities:
 * 1. DocumentRepositoryPort: Handles persistence and retrieval of document entities.
 * 2. StoragePort: Manages document storage and retrieval operations.
 * 3. PdfProcessorPort: Extracts text from document files.
 * 4. EmbeddingPort: Generates vector embeddings from extracted document text.
 *
 * The primary functionality of this service revolves around the `processDocument` method,
 * which orchestrates the processing flow for a given document.
 *
 * Key responsibilities:
 * - Find the document by its ID and validate its existence.
 * - Update the document's state throughout the processing stages.
 * - Extract text content from the document using the PdfProcessorPort.
 * - Generate embeddings based on the extracted text using the EmbeddingPort.
 * - Handle failure scenarios and appropriately update the document's state.
 *
 * This service ensures that the document's processing flow is transactional and
 * consistently maintains the integrity of the document's state in the system.
 */
@Service
public class DocumentProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessorService.class);

    private final DocumentRepositoryPort documentRepository;
    private final StoragePort storagePort;
    private final PdfProcessorPort pdfProcessor;
    private final EmbeddingPort embeddingPort;

    public DocumentProcessorService(
            DocumentRepositoryPort documentRepository,
            StoragePort storagePort,
            PdfProcessorPort pdfProcessor,
            EmbeddingPort embeddingPort) {
        this.documentRepository = documentRepository;
        this.storagePort = storagePort;
        this.pdfProcessor = pdfProcessor;
        this.embeddingPort = embeddingPort;
    }

    @Transactional
    public void processDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        try {
            logger.info("Iniciando processamento do documento: {}", documentId);

            document.startProcessing();
            document = updateDocumentState(document);

            document = processTextExtraction(document);
            document = processEmbeddingGeneration(document);

            document.completeProcessing();
            updateDocumentState(document);

            logger.info("Documento processado com sucesso: {}", documentId);

        } catch (Exception e) {
            logger.error("Erro ao processar documento: {}", documentId, e);
            handleProcessingFailure(document, e);
        }
    }

    private Document updateDocumentState(Document document) {
        return documentRepository.save(document);
    }

    private Document processTextExtraction(Document document) throws Exception {
        String extractedText = extractTextFromDocument(document);
        Document updatedDocument = document.withExtractedText(extractedText);
        return updateDocumentState(updatedDocument);
    }

    private Document processEmbeddingGeneration(Document document) throws SQLException {
        Float[] embedding = embeddingPort.generateEmbedding(document.getExtractedText());
        Document updatedDocument = document.withEmbedding(new PGvector(Arrays.toString(embedding)));
        return updateDocumentState(updatedDocument);
    }

    private void handleProcessingFailure(Document document, Exception e) {
        document.failProcessing(e.getMessage());
        updateDocumentState(document);
    }

    private String extractTextFromDocument(Document document) throws Exception {
        try (InputStream inputStream = storagePort.retrieve(document.getStorageKey())) {
            return pdfProcessor.extractText(inputStream);
        }
    }
}
