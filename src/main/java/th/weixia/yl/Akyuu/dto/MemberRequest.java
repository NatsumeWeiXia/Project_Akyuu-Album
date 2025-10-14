package th.weixia.yl.Akyuu.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class MemberRequest {
    @NotNull
    private Long userId;
    
    private String role = "MEMBER";
}