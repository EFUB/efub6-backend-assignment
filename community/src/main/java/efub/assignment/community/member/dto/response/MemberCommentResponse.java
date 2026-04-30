package efub.assignment.community.member.dto.response;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.comment.dto.response.CommentResponse;
import efub.assignment.community.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberCommentResponse {
    private final String memberNickname;
    private final List<CommentResponse> memberCommentList;
    private final Long count;

    public static MemberCommentResponse of (Member member, List<Comment> commentList) {
        return MemberCommentResponse.builder()
                .memberNickname(member.getNickname())
                .memberCommentList(commentList.stream().map(CommentResponse::of).collect(Collectors.toList()))
                .count((long)commentList.size())
                .build();
    }
}
