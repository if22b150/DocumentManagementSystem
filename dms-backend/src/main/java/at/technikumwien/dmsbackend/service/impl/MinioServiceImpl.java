package at.technikumwien.dmsbackend.service.impl;

import at.technikumwien.dmsbackend.service.MinioService;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioServiceImpl(@Value("${minio.url}") String url,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey,
                        @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;

        // Ensure bucket exists
        try {
            createBucketIfNotExists();
        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket: " + e.getMessage());
        }
    }

    private void createBucketIfNotExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @Override
    public void uploadFile(String fileName, InputStream inputStream, long size, String contentType) throws Exception {
        // Ensure bucket exists before uploading
        createBucketIfNotExists();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    @Override
    public InputStream getFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
}
