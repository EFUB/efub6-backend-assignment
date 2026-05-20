package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.request.CreateCommentRequestDto;
import efub.assignment.community.comment.dto.request.UpdateCommentRequestDto;
import efub.assignment.community.comment.dto.response.CommentListResponseDto;
import efub.assignment.community.comment.dto.response.CommentResponseDto;
import efub.assignment.community.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid CreateCommentRequestDto requestDto
    ) {
        CommentResponseDto responseDto = commentService.createComment(postId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 게시글별 댓글 목록 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentListResponseDto> getCommentsByPost(
            @PathVariable("postId") Long postId
    ) {
        CommentListResponseDto responseDto = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(responseDto);
    }

    // 회원별 댓글 목록 조회
    @GetMapping("/members/{memberId}/comments")
    public ResponseEntity<CommentListResponseDto> getCommentsByMember(
            @PathVariable("memberId") Long memberId
    ) {
        CommentListResponseDto responseDto = commentService.getCommentsByMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 1개 조회
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> getComment(
            @PathVariable("commentId") Long commentId
    ) {
        CommentResponseDto responseDto = commentService.getComment(commentId);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestHeader("Auth-Id") Long memberId,
            @RequestBody @Valid UpdateCommentRequestDto requestDto
    ) {
        CommentResponseDto responseDto = commentService.updateComment(commentId, memberId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable("commentId") Long commentId,
            @RequestHeader("Auth-Id") Long memberId
    ) {
        commentService.deleteComment(commentId, memberId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}