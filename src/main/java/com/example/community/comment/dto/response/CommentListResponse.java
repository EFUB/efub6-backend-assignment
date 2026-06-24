package com.example.community.comment.dto.response;

import java.util.List;

public record CommentListResponse(
        List<CommentResponse> comments,
        Long totalComments
) {
    public static CommentListResponse of(List<CommentResponse> comments) {
        return new CommentListResponse(comments, (long) comments.size());
    }
}
