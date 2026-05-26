package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.Message;

import java.time.LocalDateTime;

public record CreateMessageResponseDto(
        Long messageRoomId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
    public static CreateMessageResponseDto from(Message message) {
        return new CreateMessageResponseDto(
                message.getMessageRoom().getMessageRoomId(),
                message.getSender().getMemberId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}