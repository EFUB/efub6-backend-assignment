package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;
import java.time.LocalDateTime;

public record PostSearchResponse(
        Long postId,
        String content,
        String authorNickname,
        String boardName,
        LocalDateTime createdAt
) {
    public PostSearchResponse(Post post) {
        this(
                post.getId(),
                post.getContent(),
                post.getIsAnonymous()
                        ? "익명"
                        : post.getAuthor().getNickname(),
                post.getBoard().getBoardName(),
                post.getCreatedAt()
        );
    }
}