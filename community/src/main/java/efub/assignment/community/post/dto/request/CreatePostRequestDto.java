package efub.assignment.community.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequestDto(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotNull(message = "익명 여부는 필수입니다.")
        Boolean anonymous,

        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 1000, message = "내용은 1000자 이하여야 합니다.")
        String content
) {
}