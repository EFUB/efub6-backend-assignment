package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long boardId,
        Long postId,
        String nickname,
        boolean anonymous,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
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
