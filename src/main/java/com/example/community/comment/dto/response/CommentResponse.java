package com.example.community.comment.dto.response;

import com.example.community.comment.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        Boolean isAnonymous,
        Long postId,
        String nickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse from(Comment comment) {
        boolean anonymous = comment.getIsAnonymous();
        return new CommentResponse(
                comment.getCommentId(),
                comment.getContent(),
                anonymous,
                comment.getPost().getPostId(),
                anonymous ? "익명" : comment.getWriter().getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
