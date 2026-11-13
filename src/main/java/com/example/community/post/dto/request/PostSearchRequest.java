package com.example.community.post.dto.request;

import com.example.community.post.dto.condition.PostSearchCondition;
import com.example.community.post.enums.PostSort;

public record PostSearchRequest(
        String keyword,
        String authorNickname,
        PostSort postSort
) {
    public PostSearchCondition toCondition() {
        return new PostSearchCondition(
                keyword,
                authorNickname,
                postSort
        );
    }
}
