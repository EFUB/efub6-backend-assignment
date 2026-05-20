package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CreateCommentRequest;
import efub.assignment.community.comment.dto.request.UpdateCommentRequest;
import efub.assignment.community.comment.dto.response.CommentListResponse;
import efub.assignment.community.comment.dto.response.CommentResponse;
import efub.assignment.community.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable("postId") Long postId,
                                              @RequestHeader("Auth-Id") Long memberId,
                                              @Valid @RequestBody CreateCommentRequest request) {
        Long commentId = commentService.createComment(postId, memberId, request);
        return ResponseEntity.created(URI.create("/posts/"+postId+"/comments/"+commentId)).build();
    }

    // 게시글별 댓글 목록 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentListResponse> getAllCommentsByPost(@PathVariable("postId") Long postId) {

        CommentListResponse response = commentService.getAllCommentsByPost(postId);
        return ResponseEntity.ok(response);
    }

    // 작성자별 댓글 목록 조회
    @GetMapping("/members/{memberId}/comments")
    public ResponseEntity<CommentListResponse> getAllCommentsByWriter(@PathVariable("memberId") Long memberId) {

        CommentListResponse response = commentService.getAllCommentsByWriter(memberId);
        return ResponseEntity.ok(response);
    }

    // 댓글 단건 내용 조회
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateCommentContent(@PathVariable("commentId") Long commentId,
                                                  @RequestHeader("Auth-Id") Long memberId,
                                                  @RequestBody UpdateCommentRequest request) {
        commentService.updateCommentContent(commentId, request, memberId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              @RequestHeader("Auth-Id") Long memberId) {
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent().build();
    }
}
