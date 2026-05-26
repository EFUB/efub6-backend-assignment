package efub.assignment.community.messageRoom.dto.response;

import efub.assignment.community.messageRoom.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMessageRoomResponse {

    private Long messageRoomId;

    public static GetMessageRoomResponse from(MessageRoom messageRoom) {
        return GetMessageRoomResponse.builder()
                .messageRoomId(messageRoom.getId())
                .build();
    }

    public static GetMessageRoomResponse empty() {
        return GetMessageRoomResponse.builder()
                .messageRoomId(null)
                .build();
    }
}

