package th.weixia.yl.Akyuu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.weixia.yl.Akyuu.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 根据媒体ID查找所有顶级评论（parent为null）
    List<Comment> findByMediaIdAndParentIsNullOrderByCreatedAtAsc(Long mediaId);

    // 根据媒体ID查找所有评论
    List<Comment> findByMediaIdOrderByCreatedAtAsc(Long mediaId);

    // 查找特定评论的所有回复
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    // 根据媒体ID和作者ID查找评论
    List<Comment> findByMediaIdAndAuthorId(Long mediaId, Long authorId);

    // 检查用户是否有评论权限
    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.id = :commentId AND c.author.id = :userId")
    boolean isAuthorOfComment(@Param("commentId") Long commentId, @Param("userId") Long userId);

    // 获取评论数量统计
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.media.id = :mediaId")
    long countByMediaId(@Param("mediaId") Long mediaId);

    // 获取用户的评论列表
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}