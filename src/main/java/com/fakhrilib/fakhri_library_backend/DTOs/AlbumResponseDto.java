package com.fakhrilib.fakhri_library_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;

public record AlbumResponseDto(
    Long id,
    String name,
    String description,
    List<String> imageUrls,
    int imageCount,
    LocalDateTime createdAt
) {}
