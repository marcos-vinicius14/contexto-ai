package com.contextoia.documentingestion.application.port.in;

import com.contextoia.documentingestion.application.dto.SearchSimilarRequest;
import com.contextoia.documentingestion.application.dto.SimilarDocumentResponse;

import java.util.List;
import java.util.UUID;

public interface SearchSimilarDocumentsUseCase {
    List<SimilarDocumentResponse> execute(SearchSimilarRequest request, UUID userId);

}
