package efub.assignment.community.messageRoom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMessageRoomRequestDto(

        @NotNull(message = "받는 사람 id는 필수입니다.")
        Long receiverId,

        @NotNull(message = "게시글 id는 필수입니다.")
        Long postId,

        @NotBlank(message = "첫 쪽지 내용은 필수입니다.")
        String content
) {
}
