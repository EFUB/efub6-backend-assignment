package efub.assignment.community.messageRoom.dto.summary;

import java.time.LocalDateTime;

public record MessageRoomSummaryDto(
        Long messageRoomId,
        String latestMessageContent,
        LocalDateTime latestMessageCreatedAt
) {
}
