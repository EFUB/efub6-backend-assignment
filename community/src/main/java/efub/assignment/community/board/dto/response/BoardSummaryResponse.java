package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;

public record BoardSummaryResponse(
        Long boardId,
        Long memberId,
        String boardOwnerName,
        String title
) {
    public static BoardSummaryResponse from (Board board) {
        return new BoardSummaryResponse(
                board.getBoardId(),
                board.getBoardOwner().getMemberId(),
                board.getBoardOwner().getNickname(),
                board.getTitle()
        );
    }
}