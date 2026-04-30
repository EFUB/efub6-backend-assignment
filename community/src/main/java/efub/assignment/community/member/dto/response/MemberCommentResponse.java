package efub.assignment.community.member.dto.response;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.comment.dto.response.CommentResponse;
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
    private final Long memberId;
    private final List<CommentResponse> commentList;
    private final Long count;

    public static MemberCommentResponse of(Long memberId, List<Comment> commentList) {
        return MemberCommentResponse.builder()
                .memberId(memberId)
                .commentList(commentList.stream().map(CommentResponse::of).collect(Collectors.toList()))
                .count((long) commentList.size())
                .build();
    }
}
