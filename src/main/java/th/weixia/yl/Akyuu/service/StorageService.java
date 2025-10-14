package th.weixia.yl.Akyuu.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void init();

    String store(MultipartFile file, Long albumId, Long mediaId);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);
}