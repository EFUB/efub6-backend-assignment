package efub.assignment.community.message.dto.request;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMessageRequest {

    @Size(max = 1000, message = "메시지는 1000자 이내여야 합니다.")
    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String content;

    public Message toEntity(MessageRoom messageRoom, Member sender) {
        return Message.builder()
                .messageRoom(messageRoom)
                .sender(sender)
                .content(this.content)
                .build();
    }
}
