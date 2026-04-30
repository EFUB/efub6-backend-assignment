package efub.assignment.community.comment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

public record CommentUpdateRequest (
        @NotNull
        String content
) {}
