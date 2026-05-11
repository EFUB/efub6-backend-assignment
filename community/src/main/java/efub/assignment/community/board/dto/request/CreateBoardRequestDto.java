package efub.assignment.community.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBoardRequestDto(
        @NotNull(message = "멤버 id는 필수입니다.")
        Long memberId,

        @NotBlank(message = "게시판 이름은 필수입니다.")
        @Size(max = 100, message = "게시판 이름은 100자 이하여야 합니다.")
        String boardName,

        String description,

        @Size(max = 255, message = "공지는 255자 이하여야 합니다.")
        String notice
) {
}