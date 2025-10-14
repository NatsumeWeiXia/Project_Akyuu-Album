package th.weixia.yl.Akyuu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import th.weixia.yl.Akyuu.entity.User;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User user;
}