package efub.assignment.community.messageroom.dto.response;

import efub.assignment.community.messageroom.domain.MessageRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRoomDto {
    private Long messageRoomId;
    private Long creatorId;
    private Long targetId;
    private String content;
    private LocalDateTime createdAt;

    public static MessageRoomDto of (MessageRoom messageRoom) {
        return builder()
                .messageRoomId(messageRoom.getId())
                .creatorId(messageRoom.getCreator().getMemberId())
                .targetId(messageRoom.getTarget().getMemberId())
                .content(messageRoom.getFirstMessage().getContent())
                .createdAt(messageRoom.getCreatedAt())
                .build();
    }
}
