package efub.assignment.community.comment.dto.response;

import java.util.List;

public record CommentListResponse(
        List<CommentResponse> comments,
        Long totalComments
) {
}
