package com.bexos.s3_upload.controller;

import com.bexos.s3_upload.dto.ImageUploadBase64;
import com.bexos.s3_upload.dto.ImageUploadRequest;
import com.bexos.s3_upload.dto.MessageResponse;
import com.bexos.s3_upload.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse uploadImage(
            @RequestPart("file") MultipartFile file
    ) throws BadRequestException {
        ImageUploadRequest imageUploadRequest = ImageUploadRequest.builder().file(file).build();
        String imageUrl = imageService.uploadImage(imageUploadRequest);
        return MessageResponse.builder().message("Image Upload successful: " + imageUrl).build();

    }

    @PostMapping("/upload-64")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse uploadImageBase64(
            @RequestBody ImageUploadBase64 imageUploadBase64
    ) throws BadRequestException {
        String imageUrl = imageService.uploadImage(imageUploadBase64);
        return MessageResponse.builder().message("Image Upload successful: " + imageUrl).build();

    }
}
