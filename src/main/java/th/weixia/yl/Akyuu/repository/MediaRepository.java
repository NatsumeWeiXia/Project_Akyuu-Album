package th.weixia.yl.Akyuu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import th.weixia.yl.Akyuu.entity.Media;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Page<Media> findByAlbumId(Long albumId, Pageable pageable);

    List<Media> findByAlbumIdAndContentTypeStartingWith(Long albumId, String contentType);

    List<Media> findByAlbumIdOrderByCreatedAtDesc(Long albumId);

    List<Media> findByUploaderId(Long uploaderId);
}