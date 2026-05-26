package efub.assignment.community.messageRoom.dto.response;

import efub.assignment.community.messageRoom.domain.MessageRoom;

import java.time.LocalDateTime;

public record CreateMessageRoomResponseDto(
        Long messageRoomId,
        Long senderId,
        Long receiverId,
        String content,
        LocalDateTime createdAt
) {
    public static CreateMessageRoomResponseDto of(MessageRoom messageRoom,String content){
        return new CreateMessageRoomResponseDto(
                messageRoom.getMessageRoomId(),
                messageRoom.getSender().getMemberId(),
                messageRoom.getReceiver().getMemberId(),
                content,
                messageRoom.getCreatedAt()
        );
    }
}
