package com.example.community.board.dto.response;

import com.example.community.board.dto.summary.BoardSummary;

import java.util.List;

public record BoardListResponse(
        List<BoardSummary> boards,
        Long totalBoards
) {
    public static BoardListResponse of(List<BoardSummary> boards) {
        return new BoardListResponse(boards, (long) boards.size());
    }
}
