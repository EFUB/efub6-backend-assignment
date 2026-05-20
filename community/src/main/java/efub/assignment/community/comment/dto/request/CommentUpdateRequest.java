package efub.assignment.community.comment.dto.request;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateRequest {

        @NotNull
        public String content;

        // member와 post에서 가져와 사용
        public Comment toEntity() {
                return Comment.builder()
                        .content(content)
                        .build();
        }
}