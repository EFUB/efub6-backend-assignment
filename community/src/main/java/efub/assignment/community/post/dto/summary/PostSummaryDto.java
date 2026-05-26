package efub.assignment.community.post.dto.summary;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostSummaryDto(
        Long postId,
        String title,
        Boolean anonymous,
        Long viewCount,
        Long likeCount
) {
    public static PostSummaryDto from(Post post,Long likeCount) {
        return new PostSummaryDto(
                post.getPostId(),
                post.getTitle(),
                post.getAnonymous(),
                post.getViewCount(),
                likeCount
                );
    }
}