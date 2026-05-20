package efub.assignment.community.messageroom.dto.response;

import efub.assignment.community.messageroom.domain.MessageRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRoomListDto {
    private List<MessageRoomDto> messageRoomList;
    private Long messageRoomCount;

    public static MessageRoomListDto of (List<MessageRoom> messageRooms) {
        return builder()
                .messageRoomList(messageRooms.stream().map(MessageRoomDto::of).toList())
                .messageRoomCount((long) messageRooms.size())
                .build();
    }
}
