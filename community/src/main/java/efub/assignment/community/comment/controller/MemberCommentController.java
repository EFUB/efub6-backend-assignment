package efub.assignment.community.comment.controller;

import efub.assignment.community.comment.dto.response.CommentListResponseDto;
import efub.assignment.community.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{memberId}/comments")
@RequiredArgsConstructor
public class MemberCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<CommentListResponseDto> getCommentsByMember(
            @PathVariable("memberId") Long memberId
    ) {
        CommentListResponseDto responseDto = commentService.getCommentsByMember(memberId);
        return ResponseEntity.ok(responseDto);
    }
}