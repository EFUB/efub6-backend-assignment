package efub.assignment.community.messageroom.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRoomRequest {
    private Long postId;
    private Long receiverId;
}
