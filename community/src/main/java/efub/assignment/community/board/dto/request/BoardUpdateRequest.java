package efub.assignment.community.board.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardUpdateRequest {
    Long memberId;
}
