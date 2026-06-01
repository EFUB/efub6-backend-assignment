package com.example.community.comment.dto.response;

import com.example.community.comment.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        Boolean isAnonymous,
        Long postId,
        Long memberId,
        String nickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getIsAnonymous(),
                comment.getPost().getPostId(),
                comment.getWriter().getMemberId(),
                comment.getIsAnonymous() ? "익명" : comment.getWriter().getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
