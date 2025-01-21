package com.bexos.s3_upload.service.impl;

import com.bexos.s3_upload.dto.ImageUploadBase64;
import com.bexos.s3_upload.dto.ImageUploadRequest;
import com.bexos.s3_upload.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final S3Client s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.region}")
    private String awsRegion;

    public String uploadImage(ImageUploadBase64 imageUploadRequest) throws BadRequestException {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageUploadRequest.image());

            String mimeType = detectMimeType(imageBytes);

            if (!mimeType.startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type. Only images are allowed.");
            }
            String fileExtension = getFileExtension(mimeType);
            String objectKey = UUID.randomUUID() + "." + fileExtension;

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .contentType(mimeType)
                            .build(),
                    RequestBody.fromBytes(imageBytes));

            return generateS3Url(objectKey);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Image upload failed: " + e.getMessage());
        }
    }

    public String uploadImage(ImageUploadRequest imageUploadRequest) throws BadRequestException {
        validateFileType(imageUploadRequest.file());
        String objectKey = UUID.randomUUID() + "-" + imageUploadRequest.file().getOriginalFilename();

        System.out.println("Filename: " + imageUploadRequest.file().getOriginalFilename());
        System.out.println("ContentType: " + imageUploadRequest.file().getContentType());
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .contentType("image/jpeg")
                            .build(),
                    RequestBody.fromBytes(imageUploadRequest.file().getBytes()));

            return generateS3Url(objectKey);
        } catch (IOException e) {
            throw new BadRequestException("Image upload failed: " + e.getMessage());
        }
    }

    private void validateFileType(MultipartFile file) throws BadRequestException {
        String contentType = file.getContentType();
        if (!(contentType != null && contentType.startsWith("image/"))) {
            throw new BadRequestException("Unsupported file format: " + contentType);
        }
    }

    private String detectMimeType(byte[] imageBytes) {
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return URLConnection.guessContentTypeFromStream(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to detect MIME type of the image.");
        }
    }

    private String getFileExtension(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> throw new IllegalArgumentException("Unsupported image type: " + mimeType);
        };
    }

    private String generateS3Url(String key) {
        return "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com/" + key;
    }
}
