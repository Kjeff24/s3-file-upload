# S3 Image Upload Service

## Overview
This project provides a REST API for uploading images to an Amazon S3 bucket. It supports both multipart file uploads and Base64-encoded image uploads. The service validates image types and generates a public URL for each uploaded image.

## Technologies Used
- **Spring Boot** - Backend framework
- **Amazon S3 SDK** - For S3 operations
- **Lombok** - Reduces boilerplate code
- **Maven** - Dependency management

## Features
- Upload images using multipart file format (`/upload` endpoint)
- Upload images using Base64 encoding (`/upload-64` endpoint)
- Validate file type before uploading
- Generate public URLs for uploaded images

## Installation & Setup

### Prerequisites
- Java 17+
- AWS account with S3 bucket
- AWS SDK credentials configured

### Configuration
Update `application.properties` with your AWS credentials:
```properties
aws.s3.bucket=your-bucket-name
aws.region=your-region
```

## API Endpoints

### 1. Upload Image (Multipart File)
#### Endpoint
```http
POST /api/images/upload
```
#### Request
- Content-Type: `multipart/form-data`
- Parameter: `file` (MultipartFile)

#### Response
```json
{
  "message": "Image Upload successful: <S3_URL>"
}
```

### 2. Upload Image (Base64)
#### Endpoint
```http
POST /api/images/upload-64
```
#### Request Body
```json
{
  "image": "<Base64-Encoded String>"
}
```
#### Response
```json
{
  "message": "Image Upload successful: <S3_URL>"
}
```

## Project Structure
```
com.bexos.s3_upload
├── controller
│   ├── ImageController.java
├── service
│   ├── ImageService.java
│   ├── impl
│   │   ├── ImageServiceImpl.java
├── dto
│   ├── ImageUploadBase64.java
│   ├── ImageUploadRequest.java
│   ├── MessageResponse.java
```

## Error Handling
- If an invalid file type is uploaded, a `BadRequestException` is thrown.
- If an upload fails, a `BadRequestException` with an error message is returned.

## License
This project is open-source and available for modification and distribution.

