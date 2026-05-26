package efub.assignment.community.message.dto.summary;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;

import java.time.LocalDateTime;
import java.util.List;

public record MessageRoomSummary(
        Long messageRoomId,
        String recentMessage,
        LocalDateTime recentSentAt
) {
    public static MessageRoomSummary of(MessageRoom messageRoom, Message recentMessage) {
        return new MessageRoomSummary(
                messageRoom.getId(),
                recentMessage.getContent(),
                recentMessage.getCreatedAt()
        );
    }
}
