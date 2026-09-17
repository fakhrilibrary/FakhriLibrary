package com.fakhrilib.fakhri_library_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;

public record BookResponseDto(
    Long id,
    String title,
    String author,
    String publisher,
    Integer publicationYear,
    String isbn,
    String coverImageUrl,
    String pdfUrl,
    String sourceLink,
    List<String> additionalImages,
    LocalDateTime createdAt
) {}