package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.Message;

import java.time.LocalDateTime;

public record MessageResponse (
        Long messageRoomId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
    public static MessageResponse from(Message message) {

        return new MessageResponse(
                message.getMessageRoom().getId(),
                message.getSender().getMemberId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
