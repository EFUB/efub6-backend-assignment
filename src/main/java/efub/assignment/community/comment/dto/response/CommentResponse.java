package efub.assignment.community.comment.dto.response;

import efub.assignment.community.comment.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse (
        Long commentId,
        Long memberId,
        String nickName,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
){
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getWriter().getMemberId(),
                comment.getWriter().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}
