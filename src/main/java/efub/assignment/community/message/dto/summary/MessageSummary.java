package efub.assignment.community.message.dto.summary;

import efub.assignment.community.message.domain.Message;

import java.time.LocalDateTime;

public record MessageSummary (
        String content,
        LocalDateTime createdAt,
        boolean isReceived
) {
    public static MessageSummary of(Message message, Long memberId) {
        boolean isReceived = !message.getSender().getMemberId().equals(memberId);

        return new MessageSummary(
                message.getContent(),
                message.getCreatedAt(),
                isReceived
        );
    }
}
