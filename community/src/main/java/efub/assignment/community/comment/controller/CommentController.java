package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CommentRequest;
import efub.assignment.community.comment.dto.response.CommentResponse;
import efub.assignment.community.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    //댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request
    ) {
        CommentResponse response = commentService.updateComment(commentId, request);

        return ResponseEntity.ok(response);
    }
}