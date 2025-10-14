package th.weixia.yl.Akyuu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import th.weixia.yl.Akyuu.entity.Album;
import th.weixia.yl.Akyuu.entity.Media;
import th.weixia.yl.Akyuu.entity.User;
import th.weixia.yl.Akyuu.repository.AlbumRepository;
import th.weixia.yl.Akyuu.repository.MediaRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaService {
    
    private final MediaRepository mediaRepository;
    private final AlbumRepository albumRepository;
    private final StorageService storageService;
    
    @Transactional
    public Media uploadMedia(MultipartFile file, Long albumId, String title, User uploader) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("相册不存在"));
        
        // 权限检查：公共相册或用户有权限访问
        if (!album.getIsPublic() && !album.getOwner().getId().equals(uploader.getId())) {
            throw new RuntimeException("没有权限上传到此相册");
        }
        
        Media media = new Media();
        media.setAlbum(album);
        media.setUploader(uploader);
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
            
            // 提取元数据（图片宽高/视频时长等）
            // TODO: 实现元数据提取
            
            return mediaRepository.save(savedMedia);
        } catch (Exception e) {
            // 如果存储失败，删除数据库记录
            mediaRepository.delete(savedMedia);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    public Optional<Media> getMedia(Long id) {
        return mediaRepository.findById(id);
    }
    
    public List<Media> getMediaByAlbum(Long albumId) {
        return mediaRepository.findByAlbumIdOrderByCreatedAtDesc(albumId);
    }
    
    @Transactional
    public void deleteMedia(Long id, User currentUser) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("媒体文件不存在"));
        
        // 权限检查：只有上传者或相册所有者可以删除
        if (!media.getUploader().getId().equals(currentUser.getId()) && 
            !media.getAlbum().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("没有权限删除此文件");
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