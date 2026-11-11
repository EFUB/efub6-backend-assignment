package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;

import java.util.List;

public record BoardListResponseDto(
        List<BoardSummaryResponse> boardList,
        int totalCount
) {
    public static BoardListResponseDto from(List<Board> boards) {
        return new BoardListResponseDto(
                boards.stream().map(BoardSummaryResponse::from).toList(),
                boards.size()
        );
    }
}