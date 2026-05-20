package efub.assignment.community.messageroom.dto.response;

import efub.assignment.community.messageroom.domain.Message;
import efub.assignment.community.messageroom.domain.MessageRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor (access = AccessLevel.PRIVATE)
public class MessageListDto {
    private Long messageRoomId;
    private List<MessageResponseDto> messageList;
    private Long opponentId;
    private Long messageCount;

    public static MessageListDto of (List<Message> messages, MessageRoom messageRoom, Long requesterId, Long opponentId) {
        return builder()
                .messageRoomId(messageRoom.getId())
                .messageList(messages.stream()
                        .map(message -> MessageResponseDto.of(message, message.getSender().getMemberId().equals(requesterId)
                        )).toList())
                .opponentId(opponentId)
                .messageCount((long) messages.size())
                .build();
    }
}
