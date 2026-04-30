package efub.assignment.community.post.dto.request;

import jakarta.validation.constraints.Size;

public record PostUpdateRequest(
        @Size(min=1, max=1000, message="내용은 1자 이상 1000자 이하로 작성해야 합니다.")
        String content
) {}
