package com.lucas.ctrlSave.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3SaveUploaderService {

    @Value("${aws.access-key}")
    private String ACCESS_KEY;

    @Value("${aws.secret-key}")
    private String SECRET_KEY;

    @Value("${aws.region}")
    private String REGION;

    @Value("${aws.bucket-name}")
    private String BUCKET_NAME;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        s3Client = S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
                ))
                .build();
    }

    public String upload(MultipartFile multipartFile) throws IOException {
        File file = convertToFile(multipartFile);
        String keyName = multipartFile.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(keyName)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));
        return getPublicUrl(keyName);
    }

    public void delete(String keyName) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(keyName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    private File convertToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("upload", null);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private String getPublicUrl(String keyName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, keyName);
    }
}
