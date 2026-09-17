package com.fakhrilib.fakhri_library_backend.DTOs;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoLinkDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Platform is required")
    private String platform;

    @NotBlank(message = "Source URL is required")
    private String sourceUrl;

    private LocalDateTime createdAt;
    private String createdBy;
}
