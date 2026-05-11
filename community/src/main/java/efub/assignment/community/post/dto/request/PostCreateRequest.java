package efub.assignment.community.post.dto.request;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotNull(message = "익명 여부는 필수입니다.")
    private Boolean isAnonymous;

    @Size(min = 5, max = 500, message = "내용은 5자 이상 500자 이하로 입력해야 합니다.")
    private String content;

    public Post toEntity(Member author, Board board) {
        return Post.builder()
                .author(author)
                .board(board)
                .isAnonymous(isAnonymous)
                .content(content)
                .build();
    }
}