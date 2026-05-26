package efub.assignment.community.message.dto.summary;

import efub.assignment.community.message.domain.Message;

import java.time.LocalDateTime;

public record MessageSummaryDto(
        String content,
        LocalDateTime createdAt,
        Boolean isMine
) {
    public static MessageSummaryDto from(Message message, Long memberId) {
        return new MessageSummaryDto(
                message.getContent(),
                message.getCreatedAt(),
                message.getSender().getMemberId().equals(memberId)
        );
    }
}