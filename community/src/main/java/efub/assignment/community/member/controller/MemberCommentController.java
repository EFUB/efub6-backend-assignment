package efub.assignment.community.member.controller;

import efub.assignment.community.comment.service.CommentService;
import efub.assignment.community.member.dto.response.MemberCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/comments")
public class MemberCommentController {

    private final CommentService commentService;

    // 특정 사용자 댓글 조회
    @GetMapping
    public ResponseEntity<MemberCommentResponse> getMemberComments(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(commentService.getMemberCommentList(memberId));
    }
}
