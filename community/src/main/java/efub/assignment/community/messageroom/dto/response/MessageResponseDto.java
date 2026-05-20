package efub.assignment.community.messageroom.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import efub.assignment.community.messageroom.domain.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageResponseDto {
    private Long messageRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL) //아래를 생성 후 응답때 뺄 방법...?
    private Boolean isMine;

    public static MessageResponseDto of (Message message) {
        return builder()
                .content(message.getContent())
                .messageRoomId(message.getMessageRoom().getId())
                .senderId(message.getSender().getMemberId())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static MessageResponseDto of (Message message, boolean isMine) {
        return builder()
                .content(message.getContent())
                .messageRoomId(message.getMessageRoom().getId())
                .senderId(message.getSender().getMemberId())
                .createdAt(message.getCreatedAt())
                .isMine(isMine)
                .build();
    }
}
