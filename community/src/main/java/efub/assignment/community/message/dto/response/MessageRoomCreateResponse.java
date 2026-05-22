package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageRoomCreateResponse {

    private Long messageRoomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;

    public static MessageRoomCreateResponse from(MessageRoom messageRoom, Message message) {
        return MessageRoomCreateResponse.builder()
                .messageRoomId(messageRoom.getMessageRoomId())
                .senderId(messageRoom.getSender().getMemberId())
                .receiverId(messageRoom.getReceiver().getMemberId())
                .content(message.getContent())
                .createdAt(messageRoom.getCreatedAt())
                .build();
    }
}