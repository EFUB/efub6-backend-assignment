package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CommentRequest;
import efub.assignment.community.comment.dto.request.CommentUpdateRequest;
import efub.assignment.community.comment.service.CommentService;
import efub.assignment.community.comment.dto.response.MemberCommentResponse;
import efub.assignment.community.comment.dto.response.PostCommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateCommentContent(@PathVariable("commentId") Long commentId,
                                                     @RequestHeader("auth-id") Long memberId,
                                                     @RequestBody CommentUpdateRequest request) {
        commentService.updateCommentContent(memberId, commentId,request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment (@PathVariable("commentId") Long commentId,
                                               @RequestHeader("auth-id") Long memberId) {
        commentService.deleteComment(commentId,memberId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<String> likeComment (@PathVariable("commentId") Long commentId,
                                               @RequestHeader("auth-id") Long memberId) {
        commentService.likeComment(commentId,memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body("좋아요를 눌렀습니다.");
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<String> unlikeComment (@PathVariable("commentId") Long commentId,
                                               @RequestHeader("auth-id") Long memberId) {
        commentService.unlikeComment(commentId,memberId);

        return ResponseEntity.ok("좋아요가 취소되었습니다.");
    }

    //[댓글 생성]
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@Valid @PathVariable("postId") Long postId,
                                              @RequestBody CommentRequest request) {
        Long id = commentService.createComment(postId, request);

        return ResponseEntity.created(URI.create("/posts/"+postId+"/comments/"+id)).build();
    }

    //[게시글별 댓글 조회]
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<PostCommentResponse> getComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.getPostCommentList(postId));

    }

    //[멤버별 댓글 조회]
    @GetMapping("/members/{memberId}/comments")
    public ResponseEntity<MemberCommentResponse> getMemberComments(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(commentService.getMemberCommentList(memberId));
    }
}
