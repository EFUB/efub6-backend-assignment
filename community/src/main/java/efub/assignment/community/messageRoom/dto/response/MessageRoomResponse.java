package efub.assignment.community.messageRoom.dto.response;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageRoomResponse {

    private Long messageRoomId;
    private String latestMessage;
    LocalDateTime createdAt;

    public static MessageRoomResponse from(MessageRoom messageRoom, Message latestMessage) {
        return MessageRoomResponse.builder()
                .messageRoomId(messageRoom.getId())
                .latestMessage(latestMessage.getMessage())
                .createdAt(latestMessage.getCreatedAt())
                .build();
    }
}
