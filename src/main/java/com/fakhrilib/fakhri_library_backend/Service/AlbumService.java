package com.fakhrilib.fakhri_library_backend.Service;

import com.fakhrilib.fakhri_library_backend.DTOs.AlbumResponseDto;
import com.fakhrilib.fakhri_library_backend.Respository.AlbumRepository;
import com.fakhrilib.fakhri_library_backend.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final CloudinaryService cloudinaryService;

    public AlbumService(AlbumRepository albumRepository, CloudinaryService cloudinaryService) {
        this.albumRepository = albumRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Page<AlbumResponseDto> getAllAlbums(int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return albumRepository.findAll(pr).map(this::toDto);
    }

    public AlbumResponseDto getAlbumById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found: " + id));
        return toDto(album);
    }

    public AlbumResponseDto createAlbum(String name, String description, List<MultipartFile> imageFiles) {
        Album album = new Album();
        album.setName(name.trim());
        album.setDescription(description != null ? description.trim() : null);
        album.setImageUrls(new ArrayList<>());
        if (imageFiles != null) {
            for (MultipartFile f : imageFiles) {
                if (f != null && !f.isEmpty()) {
                    album.getImageUrls().add(cloudinaryService.uploadGalleryImage(f));
                }
            }
        }
        return toDto(albumRepository.save(album));
    }

    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }

    private AlbumResponseDto toDto(Album a) {
        return new AlbumResponseDto(a.getId(), a.getName(), a.getDescription(),
                a.getImageUrls(), a.getImageUrls().size(), a.getCreatedAt());
    }
}
