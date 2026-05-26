package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.dto.summary.MessageSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageListResponse {

    private Long messageRoomId;
    private Long receiverId;
    private List<MessageSummary> messages;
}