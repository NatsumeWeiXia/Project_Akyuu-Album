package th.weixia.yl.Akyuu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import th.weixia.yl.Akyuu.dto.CommentRequest;
import th.weixia.yl.Akyuu.entity.Comment;
import th.weixia.yl.Akyuu.entity.Media;
import th.weixia.yl.Akyuu.entity.User;
import th.weixia.yl.Akyuu.repository.CommentRepository;
import th.weixia.yl.Akyuu.repository.MediaRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MediaRepository mediaRepository;
    private final AlbumMemberService albumMemberService;

    @Transactional
    public Comment createComment(Long mediaId, CommentRequest request, User currentUser) {
        // 检查媒体是否存在
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "媒体文件不存在"));

        // 检查用户是否有权限评论（需要能访问相册）
        if (!albumMemberService.canAccessAlbum(media.getAlbumId(), currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权评论此媒体");
        }

        Comment comment = new Comment();
        comment.setMedia(media);
        comment.setAuthor(currentUser);
        comment.setContent(request.getContent());

        // 处理回复评论
        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "父评论不存在"));

            // 检查回复的评论是否属于同一个媒体
            if (!parentComment.getMedia().getId().equals(mediaId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "回复的评论与媒体不匹配");
            }

            comment.setParent(parentComment);
        }

        return commentRepository.save(comment);
    }

    public List<Comment> getMediaComments(Long mediaId, User currentUser) {
        // 检查媒体是否存在
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "媒体文件不存在"));

        // 检查用户是否有权限查看评论
        if (!albumMemberService.canAccessAlbum(media.getAlbumId(), currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权查看此媒体的评论");
        }

        // 获取所有顶级评论及其回复
        List<Comment> topLevelComments = commentRepository.findByMediaIdAndParentIsNullOrderByCreatedAtAsc(mediaId);

        // 为每个顶级评论加载回复
        for (Comment comment : topLevelComments) {
            List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
            comment.setReplies(replies);
        }

        return topLevelComments;
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentRequest request, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在"));

        // 检查权限：只有评论作者可以修改
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有评论作者可以修改评论");
        }

        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在"));

        Media media = comment.getMedia();

        // 检查删除权限：评论作者、媒体上传者、相册所有者都可以删除
        boolean canDelete = comment.getAuthor().getId().equals(currentUser.getId()) ||
                media.getUploaderId().equals(currentUser.getId()) ||
                albumMemberService.canManageAlbum(media.getAlbumId(), currentUser.getId());

        if (!canDelete) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此评论");
        }

        // 如果是顶级评论，先删除所有回复
        if (comment.getParent() == null) {
            List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
            commentRepository.deleteAll(replies);
        }

        commentRepository.delete(comment);
    }

    public long getCommentCount(Long mediaId) {
        return commentRepository.countByMediaId(mediaId);
    }

    public List<Comment> getUserComments(Long userId, User currentUser) {
        // 检查是否查询自己的评论或其他用户的评论
        if (!userId.equals(currentUser.getId())) {
            // 如果是查询其他用户的评论，需要检查隐私设置
            // 这里可以添加更复杂的隐私控制逻辑
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权查看其他用户的评论");
        }

        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
    }
}