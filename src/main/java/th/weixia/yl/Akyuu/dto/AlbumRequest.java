package th.weixia.yl.Akyuu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AlbumRequest {
    @NotBlank
    @Size(max = 128)
    private String name;

    private String description;

    private Boolean isPublic = true;
}