package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageRoomExistResponse {

    private Long messageRoomId;

    public static MessageRoomExistResponse from(MessageRoom messageRoom) {
        return MessageRoomExistResponse.builder()
                .messageRoomId(messageRoom.getMessageRoomId())
                .build();
    }
}