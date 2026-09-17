package com.fakhrilib.fakhri_library_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String isbn;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "source_link")
    private String sourceLink;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookImage> images;

    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private String deletedBy;

    private LocalDateTime createdAt;
    private String createdBy;
}