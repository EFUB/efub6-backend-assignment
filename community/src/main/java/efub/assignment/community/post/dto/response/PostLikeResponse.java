package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.PostLike;

import java.time.LocalDateTime;

public record PostLikeResponse(
        Long memberId,
        Long postId,
        LocalDateTime createdAt
) {
    public static PostLikeResponse from(PostLike postLike) {
        return new PostLikeResponse(
                postLike.getMember().getMemberId(),
                postLike.getPost().getId(),
                postLike.getCreatedAt()
        );
    }
}