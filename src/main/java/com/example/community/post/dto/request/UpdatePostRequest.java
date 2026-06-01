package com.example.community.post.dto.request;

import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @Size(max = 50, message = "제목은 50자 이하여야 합니다.")
        String title,

        @Size(min = 5, max = 500, message = "내용은 5자이상 500자이하로 입력해야합니다.")
        String content
) { }
