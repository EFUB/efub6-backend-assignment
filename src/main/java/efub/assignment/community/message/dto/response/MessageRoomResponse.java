package efub.assignment.community.message.dto.response;

import efub.assignment.community.message.domain.MessageRoom;

import java.time.LocalDateTime;

public record MessageRoomResponse(
        Long messageRoomId,
        Long creatorId,
        Long partnerId,
        String content,
        LocalDateTime createdAt
) {
    public static MessageRoomResponse from(MessageRoom messageRoom) {

        String firstMessageContent = messageRoom.getMessages().get(0).getContent();

        return new MessageRoomResponse(
                messageRoom.getId(),
                messageRoom.getCreator().getMemberId(),
                messageRoom.getPartner().getMemberId(),
                firstMessageContent,
                messageRoom.getCreatedAt()
        );
    }
}