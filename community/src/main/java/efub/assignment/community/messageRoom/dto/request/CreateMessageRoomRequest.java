package efub.assignment.community.messageRoom.dto.request;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateMessageRoomRequest {

    @NotBlank(message = "메시지를 입력해주세요.")
    private String message;

    @NotNull
    private Long postId;

    @NotNull
    private Long receiverId;

    public MessageRoom toEntity(Member sender, Member receiver, Post post) {
        return new MessageRoom(sender, receiver, post);
    }
}
