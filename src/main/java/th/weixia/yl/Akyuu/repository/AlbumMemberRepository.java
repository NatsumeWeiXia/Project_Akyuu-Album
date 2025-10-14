package th.weixia.yl.Akyuu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.weixia.yl.Akyuu.entity.AlbumMember;

import java.util.List;
import java.util.Optional;

public interface AlbumMemberRepository extends JpaRepository<AlbumMember, Long> {
    
    @Query("SELECT am FROM AlbumMember am WHERE am.album.id = :albumId AND am.user.id = :userId")
    Optional<AlbumMember> findByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);
    
    List<AlbumMember> findByAlbumId(Long albumId);
    
    List<AlbumMember> findByUserId(Long userId);
    
    @Query("SELECT am FROM AlbumMember am WHERE am.album.id = :albumId AND am.album.ownerId = :userId")
    Optional<AlbumMember> findOwnerByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);
    
    @Query("SELECT COUNT(am) > 0 FROM AlbumMember am WHERE am.album.id = :albumId AND am.user.id = :userId")
    boolean existsByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);
    
    void deleteByAlbumIdAndUserId(Long albumId, Long userId);
}