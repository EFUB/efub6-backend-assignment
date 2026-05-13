package efub.assignment.community.comment.dto.response;

import efub.assignment.community.comment.domain.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private final Long commentId;
    private final Long postId;
    private final String authorNickname;
    private final Boolean isAnonymous;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .authorNickname(comment.getIsAnonymous() ? "익명" : comment.getWriter().getNickname())
                .isAnonymous(comment.getIsAnonymous())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}