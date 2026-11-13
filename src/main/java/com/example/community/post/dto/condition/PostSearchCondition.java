package com.example.community.post.dto.condition;

import com.example.community.post.enums.PostSort;

public record PostSearchCondition(
        String keyword,
        String authorNickname,
        PostSort postSort
) {
}
