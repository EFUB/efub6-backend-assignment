package efub.assignment.community.board.dto.request;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardCreateRequest {
    @NotNull
    private Long memberId;

    @NotBlank(message = "제목을 입력해야합니다.")
    private String title;

    private String description;

    private String notice;

    public Board toEntity(Member boardOwner) {
        return Board.builder()
                .title(title)
                .description(description)
                .notice(notice)
                .boardOwner(boardOwner)
                .build();
    }
}
