package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CommentUpdateRequest;
import efub.assignment.community.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateCommentContent(@PathVariable("commentId") Long commentId,
                                                     @RequestHeader("auth-id") Long memberId,
                                                     @RequestBody CommentUpdateRequest request) {
        commentService.updateCommentContent(memberId, commentId,request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment (@PathVariable("commentId") Long commentId,
                                               @RequestHeader("auth-id") Long memberId) {
        commentService.deleteComment(commentId,memberId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> likeComment (@PathVariable("commentId") Long commentId,
                                               @RequestHeader("auth-id") Long memberId) {
        commentService.likeComment(commentId,memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body("좋아요를 눌렀습니다.");
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<String> unlikeComment (@PathVariable("commentId") Long commentId,
                                               @RequestHeader("auth-id") Long memberId) {
        commentService.unlikeComment(commentId,memberId);

        return ResponseEntity.ok("좋아요가 취소되었습니다.");
    }
}
