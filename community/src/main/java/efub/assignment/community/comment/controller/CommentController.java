package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CommentUpdateRequest;
import efub.assignment.community.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
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
}
