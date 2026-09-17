package com.fakhrilib.fakhri_library_backend.DTOs;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record BookRequestDto(
    @NotBlank(message = "Title is required") String title,
    String author,
    String publisher,
    Integer publicationYear,
    String isbn,
    String pdfUrl,
    String sourceLink,
    List<String> additionalImages
) {}
