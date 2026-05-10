package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        Long memberId,
        String nickName,
        String title,
        String content,
        boolean isAnonymous,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long viewCount
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.isAnonymous() ? null : post.getWriter().getMemberId(),
                post.isAnonymous() ? "익명" : post.getWriter().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.isAnonymous(), // boolean은 앞에 is를 붙여야 함.
                post.getCreatedAt(),
                post.getModifiedAt(),
                post.getViewCount()
        );
    }
}