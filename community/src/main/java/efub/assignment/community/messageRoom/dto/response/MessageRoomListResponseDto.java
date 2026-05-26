package efub.assignment.community.messageRoom.dto.response;

import efub.assignment.community.messageRoom.domain.MessageRoom;
import efub.assignment.community.messageRoom.dto.summary.MessageRoomSummaryDto;

import java.util.List;

public record MessageRoomListResponseDto(
        List<MessageRoomSummaryDto> messageRooms
) {
}
