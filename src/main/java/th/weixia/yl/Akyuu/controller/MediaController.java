package th.weixia.yl.Akyuu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.weixia.yl.Akyuu.entity.Media;
import th.weixia.yl.Akyuu.entity.User;
import th.weixia.yl.Akyuu.service.MediaService;
import th.weixia.yl.Akyuu.service.StorageService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("albumId") Long albumId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        try {
            Media media = mediaService.uploadMedia(file, albumId, title, currentUser);
            return ResponseEntity.ok(media);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedia(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        try {
            Optional<Media> media = mediaService.getMedia(id, currentUser.getId());
            return media.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadMedia(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        try {
            Optional<Media> mediaOptional = mediaService.getMedia(id, currentUser.getId());
            if (!mediaOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Media media = mediaOptional.get();
            Resource resource = storageService.loadAsResource(media.getStoragePath());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + media.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(media.getContentType()))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> previewMedia(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        try {
            Optional<Media> mediaOptional = mediaService.getMedia(id, currentUser.getId());
            if (!mediaOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Media media = mediaOptional.get();
            Resource resource = storageService.loadAsResource(media.getStoragePath());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(media.getContentType()))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<?> getAlbumMedia(@PathVariable Long albumId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        try {
            List<Media> mediaList = mediaService.getMediaByAlbum(albumId, currentUser.getId());
            return ResponseEntity.ok(mediaList);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedia(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        try {
            mediaService.deleteMedia(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}