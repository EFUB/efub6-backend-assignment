package com.example.community.board.dto.request;

import com.example.community.board.domain.Board;
import com.example.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBoardRequest(
        @NotBlank(message = "게시판 이름을 입력해야 합니다.")
        @Size(max = 50, message = "게시판 이름은 50자 이하여야 합니다.")
        String name,

        @Size(max = 255, message = "게시판 설명은 255자 이하여야 합니다.")
        String description,

        @Size(max = 255, message = "공지사항은 255자 이하여야 합니다.")
        String notice
) {
    public Board toEntity(Member owner) {
        return Board.builder()
                .owner(owner)
                .name(name)
                .description(description)
                .notice(notice)
                .build();
    }
}
