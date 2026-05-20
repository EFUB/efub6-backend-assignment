package efub.assignment.community.message.dto.request;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMessageRoomRequest {

    @NotNull(message = "받는 사람 ID는 필수입니다.")
    private Long receiverId;

    @NotNull(message = "시작된 게시글 ID는 필수입니다.")
    private Long postId;

    @NotBlank(message = "첫 쪽지 내용은 필수입니다.")
    private String firstMessage;

    public MessageRoom toEntity(Member sender, Member receiver, Post post) {
            return MessageRoom.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .post(post)
                    .build();
    }
}