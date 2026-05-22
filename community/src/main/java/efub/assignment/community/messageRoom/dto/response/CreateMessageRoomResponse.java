package efub.assignment.community.messageRoom.dto.response;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateMessageRoomResponse {

    private Long messageRoomId;
    private Long senderId;
    private Long receiverId;
    private String message;
    LocalDateTime createdAt;

    public static CreateMessageRoomResponse from(MessageRoom messageRoom, Message firstMessage) {
        return CreateMessageRoomResponse.builder()
                .messageRoomId(messageRoom.getId())
                .senderId(messageRoom.getSender().getMemberId())
                .receiverId(messageRoom.getReceiver().getMemberId())
                .message(firstMessage.getMessage())
                .createdAt(messageRoom.getCreatedAt())
                .build();
    }
}
