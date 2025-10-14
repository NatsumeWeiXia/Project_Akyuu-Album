package th.weixia.yl.Akyuu.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "album_members")
@Data
public class AlbumMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Size(max = 16)
    @Column(nullable = false)
    private String role = "MEMBER";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}