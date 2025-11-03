package com.contextoia.documentingestion.infrastructure.adapter.messaging;

import com.contextoia.documentingestion.application.dto.ProcessDocumentMessage;
import com.contextoia.documentingestion.application.port.out.DocumentMessagePublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RabbitMQMessagePublisher is responsible for publishing messages to a RabbitMQ queue
 * for processing document-related operations. It acts as an implementation of the
 * DocumentMessagePublisher interface, leveraging RabbitTemplate for sending
 * messages to the specified queue.
 *
 * This class publishes messages of type ProcessDocumentMessage to a RabbitMQ
 * queue, allowing the message to be processed asynchronously by other services.
 *
 * The queue name is configured via the application properties using the
 * property key "rabbitmq.queue.pdf-processing".
 */
@Component
public class RabbitMQMessagePublisher implements DocumentMessagePublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    /**
     * Constructs a RabbitMQMessagePublisher for publishing messages to a RabbitMQ queue.
     *
     * @param rabbitTemplate the RabbitTemplate instance used for interacting with RabbitMQ
     * @param queueName the name of the RabbitMQ queue to which messages will be sent,
     *                  configured via the application property "rabbitmq.queue.pdf-processing"
     */
    public RabbitMQMessagePublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.queue.pdf-processing}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    /**
     * Publishes a message of type {@link ProcessDocumentMessage} to a configured RabbitMQ
     * queue for processing. This method uses {@link RabbitTemplate} to send the message
     * to the specified queue.
     *
     * @param message the {@link ProcessDocumentMessage} containing details about the
     *                document to be processed, including the document ID and its storage key.
     */
    @Override
    public void publishProcessingMessage(ProcessDocumentMessage message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }
}
