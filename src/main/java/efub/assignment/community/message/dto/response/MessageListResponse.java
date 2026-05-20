package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.message.dto.summary.MessageSummary;

import java.util.List;

public record MessageListResponse (
        Long messageRoomId,
        Long partnerId,
        List<MessageSummary> messages
) {
    public static MessageListResponse of(MessageRoom messageRoom, Long memberId) {
        Long partnerId = messageRoom.getSender().getMemberId().equals(memberId)
                ? messageRoom.getReceiver().getMemberId()
                : messageRoom.getSender().getMemberId();

        List<MessageSummary> messageSummaries = messageRoom.getMessages().stream()
                .map(message -> MessageSummary.of(message, memberId))
                .toList();

        return new MessageListResponse(
                messageRoom.getId(),
                partnerId,
                messageSummaries
        );
    }
}
