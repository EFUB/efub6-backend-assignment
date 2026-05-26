package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.Message;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageCreateResponse {

    private Long messageId;
    private Long messageRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;

    public static MessageCreateResponse from(Message message) {
        return MessageCreateResponse.builder()
                .messageId(message.getMessageId())
                .messageRoomId(message.getMessageRoom().getMessageRoomId())
                .senderId(message.getSender().getMemberId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}