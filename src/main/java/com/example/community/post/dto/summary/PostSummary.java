package com.example.community.post.dto.summary;

import com.example.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostSummary(
        Long postId,
        Long boardId,
        String nickname,
        Boolean isAnonymous,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostSummary from(Post post) {
        return new PostSummary(
                post.getPostId(),
                post.getBoard().getBoardId(),
                post.getIsAnonymous() ? "익명" : post.getWriter().getNickname(),
                post.getIsAnonymous(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
