package efub.assignment.community.comment.dto.response;

import efub.assignment.community.comment.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long commentId,
        Long postId,
        Long memberId,
        String writerNickname,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getPost().getPostId(),
                comment.getWriter().getMemberId(),
                comment.getWriter().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}