package efub.assignment.community.post.dto.request;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostCreateRequest {

    @NotNull
    private boolean anonymous;

    @NotBlank
    @Size(min=1, max=1000, message="내용은 1자 이상 1000자 이하로 작성해야 합니다.")
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
