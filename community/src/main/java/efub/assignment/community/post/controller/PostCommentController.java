package efub.assignment.community.post.controller;

import efub.assignment.community.comment.dto.request.CommentCreateRequest;
import efub.assignment.community.comment.service.CommentService;
import efub.assignment.community.post.dto.response.PostCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class PostCommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<Void> createComment(@PathVariable("postId") Long postId, @RequestBody CommentCreateRequest request) {
        Long id = commentService.createComment(postId, request);
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comments" + id)).build();
    }

    // 특정 게시물 댓글 조회
    @GetMapping
    public ResponseEntity<PostCommentResponse> getPostComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.getPostCommentList(postId));
    }
}
