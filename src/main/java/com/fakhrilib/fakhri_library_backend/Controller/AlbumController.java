package com.fakhrilib.fakhri_library_backend.Controller;

import com.fakhrilib.fakhri_library_backend.DTOs.AlbumResponseDto;
import com.fakhrilib.fakhri_library_backend.Service.AlbumService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "*")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponseDto>> getAllAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(albumService.getAllAlbums(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getAlbum(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponseDto> createAlbum(
            @RequestParam("name")
            @NotBlank(message = "Album name is required")
            @Size(max = 100, message = "Album name must be under 100 characters")
            String name,

            @RequestParam(value = "description", required = false)
            @Size(max = 500, message = "Description must be under 500 characters")
            String description,

            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        // Validate image count
        if (images == null || images.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (images.size() > 50) {
            return ResponseEntity.badRequest().build();
        }
        // Validate each file is an image
        for (MultipartFile f : images) {
            String ct = f.getContentType();
            if (ct == null || !ct.startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }
        }

        return new ResponseEntity<>(albumService.createAlbum(name, description, images), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
