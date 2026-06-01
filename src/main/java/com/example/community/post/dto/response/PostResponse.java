package com.example.community.post.dto.response;

import com.example.community.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        String title,
        String content,
        Boolean isAnonymous,
        Long boardId,
        String boardName,
        String nickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        boolean anonymous = post.getIsAnonymous();
        return new PostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                anonymous,
                post.getBoard().getBoardId(),
                post.getBoard().getName(),
                anonymous ? "익명" : post.getWriter().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
