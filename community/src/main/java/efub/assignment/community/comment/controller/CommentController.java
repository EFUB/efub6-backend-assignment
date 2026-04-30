package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CommentUpdateRequest;
import efub.assignment.community.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/{commentId}")
public class CommentController {

    private final CommentService commentService;

    // 댓글 수정
    @PatchMapping
    public ResponseEntity<Void> updateComment(@PathVariable("commentId") Long commentId,
                                              @RequestHeader("Auth-Id") Long memberId,
                                              @Valid @RequestBody CommentUpdateRequest request) {
        commentService.updateCommentContent(commentId, request, memberId);
        return ResponseEntity.noContent().build();
    }
}
