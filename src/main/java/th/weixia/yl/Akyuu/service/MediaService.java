package th.weixia.yl.Akyuu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import th.weixia.yl.Akyuu.entity.Album;
import th.weixia.yl.Akyuu.entity.Media;
import th.weixia.yl.Akyuu.entity.User;
import th.weixia.yl.Akyuu.repository.AlbumRepository;
import th.weixia.yl.Akyuu.repository.MediaRepository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final AlbumRepository albumRepository;
    private final StorageService storageService;
    private final AlbumMemberService albumMemberService;

    @Transactional
    public Media uploadMedia(MultipartFile file, Long albumId, String title, User uploader) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "相册不存在"));

        // 权限检查：私有相册需要所有者或成员权限
        if (!albumMemberService.canAccessAlbum(albumId, uploader.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "没有权限上传到此相册");
        }

        Media media = new Media();
        media.setAlbumId(album.getId());
        media.setUploaderId(uploader.getId());
        media.setTitle(title != null ? title : file.getOriginalFilename());
        media.setFilename(file.getOriginalFilename());
        media.setContentType(file.getContentType());
        media.setSizeBytes(file.getSize());

        // 先保存到数据库获取ID
        Media savedMedia = mediaRepository.save(media);

        try {
            // 存储文件
            String storagePath = storageService.store(file, albumId, savedMedia.getId());
            savedMedia.setStoragePath(storagePath);
            return mediaRepository.save(savedMedia);
        } catch (Exception e) {
            // 如果存储失败，删除数据库记录
            mediaRepository.delete(savedMedia);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    public Optional<Media> getMedia(Long id, Long userId) {
        Optional<Media> mediaOptional = mediaRepository.findById(id);
        if (mediaOptional.isPresent()) {
            Media media = mediaOptional.get();
            // 检查相册访问权限
            if (!albumMemberService.canAccessAlbum(media.getAlbumId(), userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此媒体文件");
            }
        }
        return mediaOptional;
    }

    public List<Media> getMediaByAlbum(Long albumId, Long userId) {
        // 检查相册访问权限
        if (!albumMemberService.canAccessAlbum(albumId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此相册");
        }
        return mediaRepository.findByAlbumIdOrderByCreatedAtDesc(albumId);
    }

    @Transactional
    public void deleteMedia(Long id, User currentUser) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "媒体文件不存在"));

        Album album = albumRepository.findById(media.getAlbumId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "相册不存在"));

        // 权限检查：只有上传者或相册所有者可以删除
        if (!media.getUploaderId().equals(currentUser.getId()) &&
                !album.getOwnerId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "没有权限删除此文件");
        }

        try {
            // 删除物理文件
            storageService.delete(media.getStoragePath());
            // 删除数据库记录
            mediaRepository.delete(media);
        } catch (Exception e) {
            throw new RuntimeException("删除文件失败", e);
        }
    }
}