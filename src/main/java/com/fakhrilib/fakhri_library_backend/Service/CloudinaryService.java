package com.fakhrilib.fakhri_library_backend.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fakhrilib.fakhri_library_backend.Exceptation.FileUploadException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "image", "folder", "fakhri_library/covers")
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload image file to Cloudinary", e);
        }
    }

    public String uploadGalleryImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "image", "folder", "fakhri_library/gallery")
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload gallery image to Cloudinary", e);
        }
    }

    public String uploadPdf(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "raw", "folder", "fakhri_library/pdfs")
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload PDF file to Cloudinary", e);
        }
    }
}
