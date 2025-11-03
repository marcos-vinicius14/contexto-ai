package com.contextoia.documentingestion.application.port.out;

import java.io.IOException;
import java.io.InputStream;

public interface PdfProcessorPort {
    String extractText(InputStream inputStream) throws IOException;
}
