package efub.assignment.community.post.dto.summary;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostSummaryDto(
        Long postId,
        String title,
        Boolean anonymous,
        Long viewCount
) {
    public static PostSummaryDto from(Post post) {
        return new PostSummaryDto(
                post.getPostId(),
                post.getTitle(),
                post.getAnonymous(),
                post.getViewCount()
       );
    }
}