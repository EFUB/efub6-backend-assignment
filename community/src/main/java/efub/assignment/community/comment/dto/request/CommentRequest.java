package efub.assignment.community.comment.dto.request;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRequest {

    private Boolean isAnonymous;
    private String content;

    public Comment toEntity(Member writer, Post post) {
        return Comment.builder()
                .writer(writer)
                .post(post)
                .isAnonymous(isAnonymous)
                .content(content)
                .build();
    }
}