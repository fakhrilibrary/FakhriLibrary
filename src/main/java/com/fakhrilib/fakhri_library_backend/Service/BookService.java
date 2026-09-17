package com.fakhrilib.fakhri_library_backend.Service;

import com.fakhrilib.fakhri_library_backend.DTOs.BookRequestDto;
import com.fakhrilib.fakhri_library_backend.DTOs.BookResponseDto;
import com.fakhrilib.fakhri_library_backend.Exceptation.ResourceNotFoundException;
import com.fakhrilib.fakhri_library_backend.Respository.BookRepository;
import com.fakhrilib.fakhri_library_backend.model.Book;
import com.fakhrilib.fakhri_library_backend.model.BookImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CloudinaryService cloudinaryService;

    public BookService(BookRepository bookRepository, CloudinaryService cloudinaryService) {
        this.bookRepository = bookRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Page<BookResponseDto> getAllActiveBooks(int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return bookRepository.findByIsDeletedFalseOrIsDeletedIsNull(pr).map(this::mapToResponse);
    }

    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
        return mapToResponse(book);
    }

    @Transactional
    public BookResponseDto createBook(BookRequestDto req, MultipartFile coverImage,
                                      MultipartFile pdfFile, List<MultipartFile> galleryImages) {
        Book book = new Book();
        book.setTitle(req.title().trim());
        book.setAuthor(req.author() != null ? req.author().trim() : null);
        book.setPublisher(req.publisher() != null ? req.publisher().trim() : null);
        book.setPublicationYear(req.publicationYear());
        book.setIsbn(req.isbn() != null ? req.isbn().trim() : null);
        book.setSourceLink(req.sourceLink() != null ? req.sourceLink().trim() : null);
        book.setCreatedAt(LocalDateTime.now());
        book.setCreatedBy("admin");
        book.setIsDeleted(false);

        if (coverImage != null && !coverImage.isEmpty()) {
            book.setCoverImageUrl(cloudinaryService.uploadImage(coverImage));
        }
        if (pdfFile != null && !pdfFile.isEmpty()) {
            book.setPdfUrl(cloudinaryService.uploadPdf(pdfFile));
        } else if (req.pdfUrl() != null && !req.pdfUrl().isBlank()) {
            book.setPdfUrl(req.pdfUrl());
        }

        List<BookImage> images = new ArrayList<>();
        if (galleryImages != null) {
            for (MultipartFile f : galleryImages) {
                if (f != null && !f.isEmpty()) {
                    BookImage img = new BookImage();
                    img.setImageUrl(cloudinaryService.uploadGalleryImage(f));
                    img.setBook(book);
                    images.add(img);
                }
            }
        }
        if (!images.isEmpty()) book.setImages(images);

        return mapToResponse(bookRepository.save(book));
    }

    @Transactional
    public void softDeleteBook(Long id) {
        Book book = bookRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
        book.setIsDeleted(true);
        book.setDeletedAt(LocalDateTime.now());
        book.setDeletedBy("admin");
        bookRepository.save(book);
    }

    private BookResponseDto mapToResponse(Book book) {
        List<String> imgs = book.getImages() != null
                ? book.getImages().stream().map(BookImage::getImageUrl).toList()
                : new ArrayList<>();
        return new BookResponseDto(book.getId(), book.getTitle(), book.getAuthor(),
                book.getPublisher(), book.getPublicationYear(), book.getIsbn(),
                book.getCoverImageUrl(), book.getPdfUrl(), book.getSourceLink(),
                imgs, book.getCreatedAt());
    }
}
