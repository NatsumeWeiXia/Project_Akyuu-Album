package th.weixia.yl.Akyuu.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 64)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Size(max = 128)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(max = 64)
    @Column(nullable = false)
    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Size(max = 16)
    @Column(nullable = false)
    private String role = "USER";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}