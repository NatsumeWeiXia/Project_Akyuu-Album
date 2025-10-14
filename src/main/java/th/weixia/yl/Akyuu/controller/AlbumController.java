package th.weixia.yl.Akyuu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import th.weixia.yl.Akyuu.dto.AlbumRequest;
import th.weixia.yl.Akyuu.entity.Album;
import th.weixia.yl.Akyuu.entity.User;
import th.weixia.yl.Akyuu.repository.AlbumRepository;
import th.weixia.yl.Akyuu.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Album> createAlbum(@Valid @RequestBody AlbumRequest request, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (albumRepository.existsByOwnerIdAndName(user.getId(), request.getName())) {
            throw new RuntimeException("Album name already exists");
        }

        Album album = new Album();
        album.setName(request.getName());
        album.setDescription(request.getDescription());
        album.setIsPublic(request.getIsPublic());
        album.setOwnerId(user.getId());

        Album savedAlbum = albumRepository.save(album);
        return ResponseEntity.ok(savedAlbum);
    }

    @GetMapping("/public")
    public ResponseEntity<Page<Album>> getPublicAlbums(Pageable pageable) {
        return ResponseEntity.ok(albumRepository.findByIsPublicTrue(pageable));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Album>> getMyAlbums(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(albumRepository.findByOwnerId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        return ResponseEntity.ok(album);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, @Valid @RequestBody AlbumRequest request, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        if (!album.getOwnerId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own albums");
        }

        album.setName(request.getName());
        album.setDescription(request.getDescription());
        album.setIsPublic(request.getIsPublic());

        Album updatedAlbum = albumRepository.save(album);
        return ResponseEntity.ok(updatedAlbum);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        if (!album.getOwnerId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own albums");
        }

        albumRepository.delete(album);
        return ResponseEntity.noContent().build();
    }
}