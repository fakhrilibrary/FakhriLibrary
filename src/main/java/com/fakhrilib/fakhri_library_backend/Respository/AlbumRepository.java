package com.fakhrilib.fakhri_library_backend.Respository;

import com.fakhrilib.fakhri_library_backend.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
}
