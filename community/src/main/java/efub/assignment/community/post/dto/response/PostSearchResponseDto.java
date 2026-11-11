package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;

public record PostSearchResponseDto(
        Long postId,
        String boardName,
        String writerNickname,
        String content
) {

    public PostSearchResponseDto(Post post) {
        this(
                post.getId(),
                post.getBoard().getBoardname(),
                post.getWriter().getNickname(),
                post.getContent()
        );
    }
}
