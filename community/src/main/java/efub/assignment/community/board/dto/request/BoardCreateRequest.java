package efub.assignment.community.board.dto.request;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BoardCreateRequest {

    @NotNull
    private Long ownerId;

    @NotBlank
    private String boardname;

    @Size(min=0, max=100, message = "내용는 100자 이하로 입력해야 합니다.")
    private String description;

    @Size(min=0, max=100, message = "내용는 100자 이하로 입력해야 합니다.")
    private String notice;

    public Board toEntity(Member member) {
        return Board.builder()
                .boardname(boardname)
                .description(description)
                .notice(notice)
                .owner(member)
                .build();
    }
}
