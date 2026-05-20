package efub.assignment.community.post.dto.summary;

import efub.assignment.community.post.domain.Post;

public record PostSummary(
        Long postId,
        String nickName,
        String title,
        Long viewCount,
        Long likeCount
) {
    public static PostSummary from(Post post) {
        return new PostSummary(
                post.getId(),
                post.isAnonymous() ? "익명" : post.getWriter().getNickname(), // 익명을 true로 하면 nickname 가려줘야 함!
                post.getTitle(),
                post.getViewCount(),
                post.getLikeCount()
        );
    }
}