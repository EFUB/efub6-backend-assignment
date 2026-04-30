package efub.assignment.community.board.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardOwnerUpdateRequest {
    @NotNull
    private Long ownerId;
}
