package efub.assignment.community.messageroom.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateMessageRoomDto {
    private String firstMessageContent;
    @NotNull
    private Long postId;
    @NotNull
    private Long targetId;
}
