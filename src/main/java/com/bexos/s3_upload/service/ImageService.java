package com.bexos.s3_upload.service;

import com.bexos.s3_upload.dto.ImageUploadBase64;
import com.bexos.s3_upload.dto.ImageUploadRequest;
import org.apache.coyote.BadRequestException;

public interface ImageService {
    String uploadImage(ImageUploadRequest imageUploadRequest) throws BadRequestException;
    String uploadImage(ImageUploadBase64 imageUploadRequest) throws BadRequestException;
}
