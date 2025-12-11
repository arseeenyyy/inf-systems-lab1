package com.github.arseeenyyy.service;

import io.minio.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
import com.github.arseeenyyy.config.MinioConfig;

@ApplicationScoped
public class MinioService {
    
    @Inject
    private MinioConfig minioConfig;
    
    private final String BUCKET = "import-files";
    
    public String saveFile(InputStream inputStream, String fileName, long fileSize) throws Exception {
        byte[] data = inputStream.readAllBytes();
        String fileKey = System.currentTimeMillis() + "_" + fileName;
        
        try {
            boolean found = minioConfig.getClient().bucketExists(
                BucketExistsArgs.builder().bucket(BUCKET).build()
            );
            if (!found) {
                minioConfig.getClient().makeBucket(
                    MakeBucketArgs.builder().bucket(BUCKET).build()
                );
            }
        } catch (Exception e) {
        }
        
        String contentType = "application/octet-stream";
        if (fileName.toLowerCase().endsWith(".json")) {
            contentType = "application/json";
        } else if (fileName.toLowerCase().endsWith(".txt")) {
            contentType = "text/plain";
        }
        
        minioConfig.getClient().putObject(
            PutObjectArgs.builder()
                .bucket(BUCKET)
                .object(fileKey)
                .stream(new java.io.ByteArrayInputStream(data), fileSize, -1)
                .contentType(contentType)
                .build()
        );
        
        return fileKey;
    }
    
    public InputStream getFile(String fileKey) throws Exception {
        return minioConfig.getClient().getObject(
            GetObjectArgs.builder()
                .bucket(BUCKET)
                .object(fileKey)
                .build()
        );
    }
}