package efub.assignment.community.post.dto.summary;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostSummary(
        Long postId,
        String authorNickname,
        Boolean isAnonymous,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long commentCount
) {
    public static PostSummary from(Post post) {
        return new PostSummary(
                post.getId(),
                post.getIsAnonymous() ? "익명" : post.getAuthor().getNickname(),
                post.getIsAnonymous(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                0L //댓글 임시
        );
    }
}