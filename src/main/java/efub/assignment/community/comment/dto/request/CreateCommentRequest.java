package efub.assignment.community.comment.dto.request;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCommentRequest {

    @NotBlank(message = "내용을 입력해야 합니다.")
    private String content;

    public Comment toEntity(Post post, Member member) {
        return Comment.builder()
                .post(post)
                .content(content)
                .writer(member)
                .build();
    }
}
