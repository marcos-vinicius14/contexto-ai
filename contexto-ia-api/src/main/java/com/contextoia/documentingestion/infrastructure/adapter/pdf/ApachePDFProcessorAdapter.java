package com.contextoia.documentingestion.infrastructure.adapter.pdf;

import com.contextoia.documentingestion.application.port.out.PdfProcessorPort;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * An adapter for processing PDF files using Apache PDFBox.
 * This class implements the {@link PdfProcessorPort} interface and provides a concrete method for
 * extracting text from a PDF input stream.
 *
 * Component ensures validation checks for encrypted files, presence of pages,
 * and successful text extraction. Any validation failure results in an {@code IOException}.
 */
@Component
public class ApachePDFProcessorAdapter implements PdfProcessorPort {

    @Override
    public String extractText(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            validateDocumentNotEncrypted(document);
            validateDocumentHasPages(document);

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            validateExtractedText(text);
            return text.trim();
        }
    }

    private void validateDocumentNotEncrypted(PDDocument document) throws IOException {
        if (document.isEncrypted()) {
            throw new IOException("PDF está criptografado e não pode ser processado");
        }
    }

    private void validateDocumentHasPages(PDDocument document) throws IOException {
        if (document.getNumberOfPages() == 0) {
            throw new IOException("PDF não contém páginas");
        }
    }

    private void validateExtractedText(String text) throws IOException {
        if (text == null || text.isBlank()) {
            throw new IOException("Não foi possível extrair texto do PDF");
        }
    }
}
