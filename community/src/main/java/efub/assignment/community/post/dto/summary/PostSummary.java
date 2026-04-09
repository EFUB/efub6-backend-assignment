package efub.assignment.community.post.dto.summary;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostSummary(
        Long boardId,
        Long postId,
        String nickname,
        boolean anonymous,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static PostSummary from(Post post) {
        return new PostSummary(
                post.getBoard().getId(),
                post.getId(),
                post.getWriter().getNickname(),
                post.isAnonymous(),
                post.getContent(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}
