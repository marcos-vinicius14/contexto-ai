package com.contextoia.documentingestion.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface StoragePort {
    String store(MultipartFile file, String fileName) throws IOException;
    InputStream retrieve(String storageKey) throws IOException;
    void delete(String storageKey) throws IOException;
    String generateFileName(String originalFileName);
}