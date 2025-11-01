package com.contextoia.documentingestion.application.port.out;

public interface EmbeddingPort {
    float[] generateEmbedding(String text);
}