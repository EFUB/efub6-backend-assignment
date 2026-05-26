package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.dto.summary.MessageSummaryDto;

import java.util.List;

public record MessageListResponseDto(
        Long messageRoomId,
        Long otherMemberId,
        List<MessageSummaryDto> messages
) {
}