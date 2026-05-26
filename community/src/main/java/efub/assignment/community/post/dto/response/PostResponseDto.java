package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long postId,
        Long boardId,
        Long memberId,
        String title,
        Boolean anonymous,
        String content,
        Long viewCount,
        Long likeCount,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static PostResponseDto from(Post post, Long likeCount) {
        Long memberId = post.getAnonymous() ? null : post.getMember().getMemberId();

        return new PostResponseDto(
                post.getPostId(),
                post.getBoard().getBoardId(),
                memberId,
                post.getTitle(),
                post.getAnonymous(),
                post.getContent(),
                post.getViewCount(),
                likeCount,
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}