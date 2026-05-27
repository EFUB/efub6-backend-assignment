package efub.assignment.community.comment.dto.response;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.member.domain.Member;

import java.util.List;

public record MemberCommentResponse(
        String memberNickname,
        List<CommentResponse> memberCommentList,
        Long count
) {
    public static MemberCommentResponse of(Member member, List<Comment> commentList) {
        return new MemberCommentResponse(
                member.getNickname(),
                commentList.stream()
                        .map(CommentResponse::of)
                        .toList(),
                (long) commentList.size()
        );
    }
}
