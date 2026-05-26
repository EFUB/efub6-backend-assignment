package efub.assignment.community.messageroom.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRoomCreateDto {
    private String firstMessageContent;
    private Long postId;
    private Long targetId;
}
