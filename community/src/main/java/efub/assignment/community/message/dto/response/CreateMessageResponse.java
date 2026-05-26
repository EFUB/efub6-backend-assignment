package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.Message;
import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;

@Getter
@Builder
public class CreateMessageResponse {

    private Long messageRoomId;
    private Long senderId;
    private String message;
    LocalDateTime createdAt;

    public static CreateMessageResponse from(Message message) {
        return CreateMessageResponse.builder()
                .messageRoomId(message.getMessageRoom().getId())
                .senderId(message.getSender().getMemberId())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
