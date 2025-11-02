package com.contextoia.documentingestion.infrastructure.adapter.storage;

import com.contextoia.documentingestion.application.port.out.StoragePort;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Adapter for MinIO storage implementation of the {@link StoragePort} interface.
 * Provides methods for storing, retrieving, deleting, and generating file names for files in MinIO storage.
 * The adapter ensures the specified bucket exists during initialization.
 */
@Component
public class MinioStorageAdapter implements StoragePort {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioStorageAdapter(
            MinioClient minioClient,
            @Value("${minio.bucket.name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar/criar bucket: " + bucketName, e);
        }
    }

    @Override
    public String store(MultipartFile file, String fileName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio");
        }

        try {
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
            return fileName;
        } catch (Exception e) {
            throw new IOException("Erro ao armazenar arquivo no MinIO", e);
        }
    }

    @Override
    public InputStream retrieve(String storageKey) throws IOException {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Erro ao recuperar arquivo do MinIO", e);
        }
    }

    @Override
    public void delete(String storageKey) throws IOException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Erro ao deletar arquivo do MinIO", e);
        }
    }

    @Override
    public String generateFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
}
