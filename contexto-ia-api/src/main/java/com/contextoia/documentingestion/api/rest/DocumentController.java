package com.contextoia.documentingestion.api.rest;

import com.contextoia.documentingestion.application.dto.DocumentDetailsResponse;
import com.contextoia.documentingestion.application.dto.DocumentUploadResponse;
import com.contextoia.documentingestion.application.dto.SearchSimilarRequest;
import com.contextoia.documentingestion.application.dto.SimilarDocumentResponse;
import com.contextoia.documentingestion.application.port.in.GetDocumentUseCase;
import com.contextoia.documentingestion.application.port.in.ListUserDocumentsUseCase;
import com.contextoia.documentingestion.application.port.in.SearchSimilarDocumentsUseCase;
import com.contextoia.documentingestion.application.port.in.UploadDocumentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * DocumentController handles API endpoints related to document management, including
 * uploading documents, retrieving document details, listing user documents, and
 * searching for documents based on similarity criteria.
 * <p>
 * This controller uses use case interfaces to comply with the principles of
 * hexagonal architecture, allowing interaction through defined application ports.
 * <p>
 * Endpoints:
 * - POST /api/documents/upload: Supports document uploads.
 * - GET /api/documents/{id}: Retrieves details of a specific document.
 * - GET /api/documents: Lists all documents associated with the authenticated user.
 * - POST /api/documents/search: Searches for similar documents based on a given query.
 * <p>
 * Exceptions are handled within each API method to ensure appropriate HTTP responses.
 * Authentication is managed through Spring Security's @AuthenticationPrincipal.
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final GetDocumentUseCase getDocumentUseCase;
    private final ListUserDocumentsUseCase listUserDocumentsUseCase;
    private final SearchSimilarDocumentsUseCase searchSimilarDocumentsUseCase;

    public DocumentController(
            UploadDocumentUseCase uploadDocumentUseCase,
            GetDocumentUseCase getDocumentUseCase,
            ListUserDocumentsUseCase listUserDocumentsUseCase,
            SearchSimilarDocumentsUseCase searchSimilarDocumentsUseCase) {
        this.uploadDocumentUseCase = uploadDocumentUseCase;
        this.getDocumentUseCase = getDocumentUseCase;
        this.listUserDocumentsUseCase = listUserDocumentsUseCase;
        this.searchSimilarDocumentsUseCase = searchSimilarDocumentsUseCase;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
        return executeWithExceptionHandling(
                () -> uploadDocumentUseCase.execute(file, userId),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDetailsResponse> getDocument(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
        return executeWithExceptionHandling(
                () -> getDocumentUseCase.execute(id, userId),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<List<DocumentDetailsResponse>> getUserDocuments(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
        List<DocumentDetailsResponse> response = listUserDocumentsUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<SimilarDocumentResponse>> searchSimilar(
            @Valid @RequestBody SearchSimilarRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
        return executeWithExceptionHandling(
                () -> searchSimilarDocumentsUseCase.execute(request, userId),
                HttpStatus.OK
        );
    }

    private UUID extractUserId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }

    private <T> ResponseEntity<T> executeWithExceptionHandling(
            Supplier<T> useCaseExecution,
            HttpStatus successStatus) {
        try {
            T result = useCaseExecution.get();
            return ResponseEntity.status(successStatus).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}