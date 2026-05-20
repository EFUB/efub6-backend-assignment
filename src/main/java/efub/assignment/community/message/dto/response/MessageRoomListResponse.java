package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.dto.summary.MessageRoomSummary;

import java.util.List;

public record MessageRoomListResponse(
        List<MessageRoomSummary> messageRooms
) {
}
