package com.contextoia.documentingestion.application.port.out;

import com.contextoia.documentingestion.application.dto.ProcessDocumentMessage;

public interface DocumentMessagePublisher {
    void publishProcessingMessage(ProcessDocumentMessage message);
}