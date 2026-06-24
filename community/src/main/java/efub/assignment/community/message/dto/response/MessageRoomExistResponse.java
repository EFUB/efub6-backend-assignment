package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MessageRoomExistResponse {

    private final Long messageRoomId;

    public MessageRoomExistResponse(Long messageRoomId) {
        this.messageRoomId = messageRoomId;
    }

    public static MessageRoomExistResponse from(MessageRoom messageRoom) {
        return new MessageRoomExistResponse(messageRoom.getMessageRoomId());
    }
}