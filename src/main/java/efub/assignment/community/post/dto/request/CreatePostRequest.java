package efub.assignment.community.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePostRequest {

    @NotBlank(message = "제목을 입력해야 합니다.")
    private String title;

    @Size(min=5, max=500, message = "내용은 5자 이상 500자 이하로 입력해야 합니다.")
    private String content;

    @NotNull
    @JsonProperty("isAnonymous") // 이거 붙이니까 에러가 안 나넹
    private Boolean isAnonymous;


    public Post toEntity(Board board, Member member) {
        return Post.builder()
                .board(board)
                .title(title)
                .content(content)
                .writer(member)
                .isAnonymous(isAnonymous)
                .build();
    }
}
