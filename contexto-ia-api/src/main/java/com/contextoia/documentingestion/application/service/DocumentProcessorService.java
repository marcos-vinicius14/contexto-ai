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
import java.util.UUID;

/**
 * Service de processamento de documentos
 * Orquestra extração de texto e geração de embeddings
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
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado"));

        try {
            logger.info("Iniciando processamento do documento: {}", documentId);

            document.startProcessing();
            documentRepository.save(document);

            String extractedText = extractTextFromDocument(document);
            document.setExtractedText(extractedText);

            float[] embedding = embeddingPort.generateEmbedding(extractedText);
            document.setEmbedding(new PGvector(embedding));

            document.completeProcessing();
            documentRepository.save(document);

            logger.info("Documento processado com sucesso: {}", documentId);

        } catch (Exception e) {
            logger.error("Erro ao processar documento: {}", documentId, e);
            document.failProcessing(e.getMessage());
            documentRepository.save(document);
        }
    }

    private String extractTextFromDocument(Document document) throws Exception {
        try (InputStream inputStream = storagePort.retrieve(document.getStorageKey())) {
            return pdfProcessor.extractText(inputStream);
        }
    }
}
