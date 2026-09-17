package com.fakhrilib.fakhri_library_backend.Respository;

import com.fakhrilib.fakhri_library_backend.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByIsDeletedFalseOrIsDeletedIsNull(Pageable pageable);
    Optional<Book> findByIdAndIsDeletedFalseOrIsDeletedIsNull(Long id);
}
