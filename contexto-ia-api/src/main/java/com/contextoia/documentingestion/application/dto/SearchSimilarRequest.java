package com.contextoia.documentingestion.application.dto;

public record SearchSimilarRequest(
        String query,
        int limit
) {
    public SearchSimilarRequest {
        if (limit <= 0 || limit > 50) {
            throw new IllegalArgumentException("Limit deve estar entre 1 e 50");
        }
    }
}
