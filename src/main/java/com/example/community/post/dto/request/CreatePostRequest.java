package com.example.community.post.dto.request;

import com.example.community.board.domain.Board;
import com.example.community.member.domain.Member;
import com.example.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotNull Boolean isAnonymous,

        @NotBlank(message = "제목을 입력해야 합니다.")
        @Size(max = 50, message = "제목은 50자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용을 입력해야 합니다.")
        @Size(min = 5, max = 500, message = "내용은 5자이상 500자이하로 입력해야합니다.")
        String content
) {
    public Post toEntity(Member writer, Board board) {
        return Post.builder()
                .writer(writer)
                .board(board)
                .isAnonymous(isAnonymous)
                .title(title)
                .content(content)
                .build();
    }
}
