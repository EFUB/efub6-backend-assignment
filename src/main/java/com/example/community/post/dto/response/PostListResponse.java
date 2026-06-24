package com.example.community.post.dto.response;

import com.example.community.post.dto.summary.PostSummary;

import java.util.List;

public record PostListResponse(
        List<PostSummary> posts,
        Long totalPosts
) {
    public static PostListResponse of(List<PostSummary> posts) {
        return new PostListResponse(posts, (long) posts.size());
    }
}
