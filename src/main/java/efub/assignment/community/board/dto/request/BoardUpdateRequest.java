package efub.assignment.community.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BoardUpdateRequest(
        @NotBlank(message = "변경할 주인의 닉네임을 입력해주세요.")
        @Pattern(regexp = "^[0-9a-zA-Z가-힣]+$", message = "닉네임에 특수문자를 포함할 수 없습니다.")
        String nickname
) {
}