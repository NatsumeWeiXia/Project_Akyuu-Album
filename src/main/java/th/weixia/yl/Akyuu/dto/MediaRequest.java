package th.weixia.yl.Akyuu.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class MediaRequest {
    @NotNull(message = "相册ID不能为空")
    private Long albumId;
    
    private String title;
    
    @NotNull(message = "文件不能为空")
    private MultipartFile file;
}