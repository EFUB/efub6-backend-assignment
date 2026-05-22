package efub.assignment.community.message.dto.summary;

import efub.assignment.community.message.domain.Message;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageSummary {

    private Long messageId;
    private Boolean isMine;
    private String content;
    private LocalDateTime createdAt;

    public static MessageSummary from(Message message, Long memberId) {
        return MessageSummary.builder()
                .messageId(message.getMessageId())
                .isMine(message.getSender().getMemberId().equals(memberId))
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}