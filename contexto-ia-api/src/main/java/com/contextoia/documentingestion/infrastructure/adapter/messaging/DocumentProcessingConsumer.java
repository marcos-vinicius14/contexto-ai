package com.contextoia.documentingestion.infrastructure.adapter.messaging;

import com.contextoia.documentingestion.application.dto.ProcessDocumentMessage;
import com.contextoia.documentingestion.application.service.DocumentProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The DocumentProcessingConsumer class is a Spring component designed to consume
 * messages from a RabbitMQ queue for processing document-related tasks.
 *
 * Messages consumed by this class are expected to be instances of
 * {@link ProcessDocumentMessage}, containing details about the document to be
 * processed. The processing logic involves invoking the
 * {@link DocumentProcessorService} to handle the document processing operations.
 *
 * This class listens to the queue configured via the "rabbitmq.queue.pdf-processing"
 * property and processes messages asynchronously.
 *
 * Key responsibilities:
 * - Listening to the PDF processing RabbitMQ queue.
 * - Extracting the document ID from the received messages.
 * - Logging the reception of messages and handling any errors during processing.
 *
 * The implementation also includes error handling mechanisms to log any failures
 * during the processing workflow, with a placeholder for implementing Dead Letter
 * Queue (DLQ) or retry logic in the future.
 */
@Component
public class DocumentProcessingConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessingConsumer.class);

    private final DocumentProcessorService documentProcessorService;

    public DocumentProcessingConsumer(DocumentProcessorService documentProcessorService) {
        this.documentProcessorService = documentProcessorService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.pdf-processing}")
    public void processDocument(ProcessDocumentMessage message) {
        UUID documentId = getDocumentId(message);

        try {
            logReceivedMessage(documentId);
            documentProcessorService.processDocument(documentId);
        } catch (Exception e) {
            handleProcessingError(documentId, e);
        }
    }

    private UUID getDocumentId(ProcessDocumentMessage message) {
        return message.documentId();
    }

    private void logReceivedMessage(UUID documentId) {
        logger.info("Mensagem recebida para processar documento: {}", documentId);
    }

    private void handleProcessingError(UUID documentId, Exception e) {
        logger.error("Erro ao processar mensagem: {}", documentId, e);
        //TODO: futuramente, Implementar DLQ ou retry conforme necessário
    }
}
