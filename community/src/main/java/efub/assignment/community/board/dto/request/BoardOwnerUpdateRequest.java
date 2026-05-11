package efub.assignment.community.board.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardOwnerUpdateRequest {

    @NotNull(message = "새로운 주인의 memberId는 필수입니다.")
    private Long memberId;
}
