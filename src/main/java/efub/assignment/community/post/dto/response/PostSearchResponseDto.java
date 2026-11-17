package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.domain.Post;

public record PostSearchResponseDto(
        Long postId,
        String title,
        String content,
        String writerNickname,
        Long viewCount
) {
    public PostSearchResponseDto(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.isAnonymous() ? "익명" : post.getWriter().getNickname(), // 익명 게시글은 닉네임 가리기
                post.getViewCount()
        );
    }
}
