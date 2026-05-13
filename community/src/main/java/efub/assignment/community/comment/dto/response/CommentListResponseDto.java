package efub.assignment.community.comment.dto.response;

import efub.assignment.community.comment.domain.Comment;

import java.util.List;

public record CommentListResponseDto(
        List<CommentResponseDto> comments,
        Long totalComments
) {
    public static CommentListResponseDto from(List<Comment> comments) {
        List<CommentResponseDto> commentResponses = comments.stream()
                .map(CommentResponseDto::from)
                .toList();

        return new CommentListResponseDto(
                commentResponses,
                (long) commentResponses.size()
        );
    }
}