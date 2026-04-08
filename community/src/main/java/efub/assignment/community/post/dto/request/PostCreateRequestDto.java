package efub.assignment.community.post.dto.request;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostCreateRequestDto {
    @NotNull
    Long boardId;

    @NotNull
    Long memberId;

    @NotBlank
    private String title;

    @Size(min=5, max=500, message = "내용은 5자이상 500자이하로 입력해야합니다.")
    private String content;

    public Post toEntity(Board board, Member member) {
        return Post.builder()
                .title(title)
                .content(content)
                .board(board)
                .writer(member)
                .build();
    }

}
