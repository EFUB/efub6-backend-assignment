package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.UpdateCommentRequestDto;
import efub.assignment.community.comment.dto.response.CommentResponseDto;
import efub.assignment.community.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> getComment(
            @PathVariable("commentId") Long commentId
    ) {
        CommentResponseDto responseDto = commentService.getComment(commentId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid UpdateCommentRequestDto requestDto
    ) {
        CommentResponseDto responseDto = commentService.updateComment(commentId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable("commentId") Long commentId
    ) {
        commentService.deleteComment(commentId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}