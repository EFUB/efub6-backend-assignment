package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        AuthorInfo author,
        BoardInfo board,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                new AuthorInfo(
                        post.getAuthor().getMemberId(),
                        post.getIsAnonymous() ? "익명" : post.getAuthor().getNickname(),
                        post.getIsAnonymous()
                ),
                new BoardInfo(
                        post.getBoard().getBoardId(),
                        post.getBoard().getBoardName()
                ),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public record AuthorInfo(
            Long memberId,
            String authorNickname,
            Boolean isAnonymous
    ) {
    }

    public record BoardInfo(
            Long boardId,
            String boardName
    ) {
    }
}