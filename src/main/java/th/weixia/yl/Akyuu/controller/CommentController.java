package th.weixia.yl.Akyuu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import th.weixia.yl.Akyuu.dto.CommentRequest;
import th.weixia.yl.Akyuu.entity.Comment;
import th.weixia.yl.Akyuu.security.UserPrincipal;
import th.weixia.yl.Akyuu.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping("/media/{mediaId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long mediaId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CommentRequest request) {
        
        Comment comment = commentService.createComment(mediaId, request, currentUser.getUser());
        return ResponseEntity.ok(comment);
    }
    
    @GetMapping("/media/{mediaId}/comments")
    public ResponseEntity<List<Comment>> getMediaComments(
            @PathVariable Long mediaId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        List<Comment> comments = commentService.getMediaComments(mediaId, currentUser.getUser());
        return ResponseEntity.ok(comments);
    }
    
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CommentRequest request) {
        
        Comment comment = commentService.updateComment(commentId, request, currentUser.getUser());
        return ResponseEntity.ok(comment);
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        commentService.deleteComment(commentId, currentUser.getUser());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/media/{mediaId}/comments/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long mediaId) {
        long count = commentService.getCommentCount(mediaId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<Comment>> getUserComments(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        List<Comment> comments = commentService.getUserComments(userId, currentUser.getUser());
        return ResponseEntity.ok(comments);
    }
}