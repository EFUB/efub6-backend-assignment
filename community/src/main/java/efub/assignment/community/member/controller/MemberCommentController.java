package efub.assignment.community.member.controller;

import efub.assignment.community.comment.service.CommentService;
import efub.assignment.community.member.dto.response.MemberCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/comments")
public class MemberCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<MemberCommentResponse> getMemberComments(@PathVariable Long memberId) {
        return ResponseEntity.ok(commentService.getMemberCommentList(memberId));
    }
}