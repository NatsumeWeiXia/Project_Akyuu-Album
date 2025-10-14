package th.weixia.yl.Akyuu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import th.weixia.yl.Akyuu.entity.Album;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Page<Album> findByIsPublicTrue(Pageable pageable);

    List<Album> findByOwnerId(Long ownerId);

    boolean existsByOwnerIdAndName(Long ownerId, String name);
}