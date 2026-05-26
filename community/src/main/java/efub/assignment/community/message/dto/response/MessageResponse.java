package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.Message;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {

    private String message;
    LocalDateTime createdAt;
    boolean isMine;

    public static MessageResponse from(Message message, Long requesterId) {
        return MessageResponse.builder()
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .isMine(message.getSender().getMemberId().equals(requesterId))
                .build();
    }
}
