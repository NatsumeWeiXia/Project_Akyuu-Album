package th.weixia.yl.Akyuu.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class MemberRequest {
    @NotNull
    private Long userId;
    
    private String role = "MEMBER";
}