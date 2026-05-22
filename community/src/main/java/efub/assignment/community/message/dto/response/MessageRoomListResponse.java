package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.dto.summary.MessageRoomSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageRoomListResponse {

    private List<MessageRoomSummary> messageRooms;
}