package efub.assignment.community.board.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateBoardRequest(
        @NotNull(message = "변경할 주인의 ID를 입력해주세요.")
        Long memberId
) {
}