package efub.assignment.community.message.dto.summary;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageRoomSummary {

    private Long messageRoomId;
    private String lastMessage;
    private LocalDateTime lastSentAt;

    public static MessageRoomSummary from(MessageRoom messageRoom, Message lastMessage) {
        return MessageRoomSummary.builder()
                .messageRoomId(messageRoom.getMessageRoomId())
                .lastMessage(lastMessage.getContent())
                .lastSentAt(lastMessage.getCreatedAt())
                .build();
    }
}