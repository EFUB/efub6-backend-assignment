package efub.assignment.community.post.dto.request;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateRequest {
    public static final int MIN_CONTENT_LENGTH = 1;
    public static final int MAX_CONTENT_LENGTH = 1000;

    @NotNull
    private boolean anonymous;

    @NotBlank
    @Size(min=MIN_CONTENT_LENGTH, max=MAX_CONTENT_LENGTH, message="내용은 1자 이상 1000자 이하로 작성해야 합니다.")
    private String content;

    public Post toEntity(Board board, Member member) {
        return Post.builder()
                .board(board)
                .writer(member)
                .anonymous(anonymous)
                .content(content)
                .build();
    }
}
