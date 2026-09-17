package com.fakhrilib.fakhri_library_backend.Controller;

import com.fakhrilib.fakhri_library_backend.Respository.VideoLinkRepository;
import com.fakhrilib.fakhri_library_backend.model.VideoLink;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
@Validated
public class VideoLinkController {

    private final VideoLinkRepository videoLinkRepository;

    public VideoLinkController(VideoLinkRepository videoLinkRepository) {
        this.videoLinkRepository = videoLinkRepository;
    }

    public record VideoRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be under 200 characters")
        String title,

        @NotBlank(message = "Platform is required")
        String platform,

        @NotBlank(message = "URL is required")
        @Size(max = 2000, message = "URL too long")
        String url
    ) {}

    @GetMapping
    public ResponseEntity<Page<VideoLink>> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(videoLinkRepository.findAll(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<VideoLink> createVideo(@Valid @RequestBody VideoRequest req) {
        VideoLink v = new VideoLink();
        v.setTitle(req.title().trim());
        v.setPlatform(req.platform().trim());
        v.setUrl(req.url().trim());
        v.setDeleted(false);
        return new ResponseEntity<>(videoLinkRepository.save(v), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoLinkRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
