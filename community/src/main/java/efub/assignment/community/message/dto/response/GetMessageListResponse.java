package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetMessageListResponse {

    private Long messageRoomId;
    private Long receiverId;
    private List<MessageResponse> messages;

    public static GetMessageListResponse from(MessageRoom messageRoom, List<Message> messageList, Long requesterId) {
        return GetMessageListResponse.builder()
                .messageRoomId(messageRoom.getId())
                .receiverId(messageRoom.getReceiver().getMemberId())
                .messages(messageList.stream()
                        .map(message -> MessageResponse.from(message, requesterId))
                        .collect(Collectors.toList()))
                .build();
    }
}
