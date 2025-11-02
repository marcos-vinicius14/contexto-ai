package com.contextoia.documentingestion.infrastructure.adapter.ai;

import com.contextoia.documentingestion.application.port.out.EmbeddingPort;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

/**
 * Adapter class that implements the {@code EmbeddingPort} interface to provide functionality
 * for generating text embeddings using an underlying embedding model.
 * This class serves as a bridge between the {@code EmbeddingModel} and the external interface,
 * handling pre-processing and validations required for embedding generation.
 * Ensures that input text adheres to a maximum length constraint before invoking the model logic.
 */
@Component
public class GeminiEmbeddingAdapter implements EmbeddingPort {

    private final EmbeddingModel embeddingModel;
    private static final int MAX_TEXT_LENGTH = 10000;

    public GeminiEmbeddingAdapter(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * Generates an embedding representation of the provided text using the underlying embedding model.
     * The input text is preprocessed to ensure it adheres to the maximum allowable length
     * before generating the embedding.
     *
     * @param text the input text to generate the embedding for. Cannot be null or blank.
     * @return an array of Float values representing the embedding vector of the input text.
     * @throws IllegalArgumentException if the input text is null or blank.
     */
    @Override
    public Float[] generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Texto não pode ser vazio");
        }

        String truncatedText = truncateText(text, MAX_TEXT_LENGTH);
        Embedding embedding = embeddingModel.embed(truncatedText).content();

        return embedding.vectorAsList().stream()
                .toList()
                .toArray(new Float[0]);
    }

    /**
     * Truncates the input text to the specified maximum length. If the text length is less than or
     * equal to the maximum length, the original text is returned unchanged. If the text length
     * exceeds the maximum length, the text is truncated to match the specified length.
     *
     * @param text the input text to be truncated. Cannot be null.
     * @param maxLength the maximum allowable length for the text. Must be a positive integer.
     * @return the truncated version of the input text if it exceeds the maximum length, or
     *         the original text if it is within the allowable length.
     */
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }
}