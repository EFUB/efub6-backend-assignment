package efub.assignment.community.board.dto.request;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.board.domain.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardCreateRequest {

    @NotBlank(message = "게시판 제목을 입력해야합니다.")
    private String boardName;

    private String description;
    private String notice;

    public Board toEntity(Member owner){
        return Board.builder()
                .boardName(boardName)
                .description(description)
                .notice(notice)
                .owner(owner)
                .build();
    }
}
