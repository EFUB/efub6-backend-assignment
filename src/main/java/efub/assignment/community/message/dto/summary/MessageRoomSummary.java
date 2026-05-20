package efub.assignment.community.message.dto.summary;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;

import java.time.LocalDateTime;
import java.util.List;

public record MessageRoomSummary(
        Long messageRoomId,
        String recentMessage,
        LocalDateTime recentSentAt
) {
    public static MessageRoomSummary from(MessageRoom messageRoom) {
        List<Message> messages = messageRoom.getMessages();
        Message recentMessage = messages.get(messages.size() - 1); // 제일 뒤에 있는 쪽지 가져오기

        return new MessageRoomSummary(
                messageRoom.getId(),
                recentMessage.getContent(),
                recentMessage.getCreatedAt()
        );
    }
}
