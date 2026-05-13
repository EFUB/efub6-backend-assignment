package efub.assignment.community.post.controller;

import efub.assignment.community.comment.dto.request.CommentRequest;
import efub.assignment.community.comment.dto.response.CommentResponse;
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

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestHeader("memberId") Long memberId,
            @RequestBody CommentRequest request
    ) {
        CommentResponse response = commentService.createComment(postId, memberId, request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<PostCommentResponse> getPostComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getPostCommentList(postId));
    }
}