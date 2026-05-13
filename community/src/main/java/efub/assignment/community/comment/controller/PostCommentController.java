package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CreateCommentRequestDto;
import efub.assignment.community.comment.dto.response.CommentListResponseDto;
import efub.assignment.community.comment.dto.response.CommentResponseDto;
import efub.assignment.community.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid CreateCommentRequestDto requestDto
    ) {
        CommentResponseDto responseDto = commentService.createComment(postId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<CommentListResponseDto> getCommentsByPost(
            @PathVariable("postId") Long postId
    ) {
        CommentListResponseDto responseDto = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(responseDto);
    }
}