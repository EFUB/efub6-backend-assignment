package efub.assignment.community.messageroom.dto.response;

import efub.assignment.community.messageroom.domain.MessageRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckMessageRoomDto {
    private Long messageRoomId;

    public static CheckMessageRoomDto of (MessageRoom messageRoom) {
        return builder()
                .messageRoomId(messageRoom.getId())
                .build();
    }
}
