package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long boardId,
        Long postId,
        Long accountId,
        String nickName,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long viewCount
) {
    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getBoard().getBoardId(),
                post.getPostId(),
                post.getWriter().getMemberId(),
                post.getWriter().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getModifiedAt(),
                post.getViewCount()
        );
    }
}