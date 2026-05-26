package efub.assignment.community.message.dto.request;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateMessageRequest {

    @NotNull
    private String message;

    public Message toEntity (MessageRoom messageRoom, Member sender, String message) {
        return new Message(messageRoom, sender, message);
    }
}
