package com.fakhrilib.fakhri_library_backend.Controller;

import com.fakhrilib.fakhri_library_backend.DTOs.BookRequestDto;
import com.fakhrilib.fakhri_library_backend.DTOs.BookResponseDto;
import com.fakhrilib.fakhri_library_backend.Service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> getAllBooks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size) {
        return ResponseEntity.ok(bookService.getAllActiveBooks(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponseDto> createBook(
            @RequestPart("book") @Valid BookRequestDto requestDto,
            @RequestPart(value = "coverImage",    required = false) MultipartFile coverImage,
            @RequestPart(value = "pdfFile",       required = false) MultipartFile pdfFile,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages) {

        // Server-side file type validation
        if (coverImage != null && !isAllowedImage(coverImage)) {
            return ResponseEntity.badRequest().build();
        }
        if (pdfFile != null && !isAllowedPdf(pdfFile)) {
            return ResponseEntity.badRequest().build();
        }

        BookResponseDto created = bookService.createBook(requestDto, coverImage, pdfFile, galleryImages);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteBook(@PathVariable Long id) {
        bookService.softDeleteBook(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isAllowedImage(MultipartFile f) {
        String ct = f.getContentType();
        return ct != null && (ct.startsWith("image/jpeg") || ct.startsWith("image/png")
                || ct.startsWith("image/webp") || ct.startsWith("image/gif"));
    }

    private boolean isAllowedPdf(MultipartFile f) {
        String ct = f.getContentType();
        return ct != null && ct.equals("application/pdf");
    }
}
