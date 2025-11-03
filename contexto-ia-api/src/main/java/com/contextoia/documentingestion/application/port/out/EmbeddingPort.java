package com.contextoia.documentingestion.application.port.out;

public interface EmbeddingPort {
    Float[] generateEmbedding(String text);
}