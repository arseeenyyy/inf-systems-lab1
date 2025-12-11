package com.github.arseeenyyy.config;

import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MinioConfig {
    
    private MinioClient minioClient;
    
    @PostConstruct
    void init() {
        this.minioClient = MinioClient.builder()
            .endpoint("http://localhost:9000")  
            .credentials(System.getenv("minio_login"), System.getenv("minio_password"))
            .build();
    }
    
    public MinioClient getClient() {
        return minioClient;
    }
}