package efub.assignment.community.comment.dto.request;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequest {

    // comment 생성 시 작성자ID, content 전달받기
    private String content;

    // member와 post에서 가져와 사용
    public Comment toEntity(Member member, Post post) {
        return Comment.builder()
                .content(content)
                .writer(member)
                .post(post)
                .build();
    }
}
