package efub.assignment.community.board.dto.request;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateBoardRequest {

    @NotBlank(message = "게시판 이름을 입력해야 합니다.")
    private String name;

    @Size(max = 1000, message = "게시판 설명은 1000자를 초과할 수 없습니다.")
    private String description;

    @Size(max = 1000, message = "게시판 공지는 1000자를 초과할 수 없습니다.")
    private String notification;

    public Board toEntity(Member member) {
        return Board.builder()
                .writer(member)
                .name(name)
                .description(description)
                .notification(notification)
                .build();
    }
}
