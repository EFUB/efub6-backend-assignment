package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;

import java.time.LocalDateTime;

public record BoardResponse(
        Long boardId,
        Long ownerId,
        String boardname,
        String description,
        String notice,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getOwner().getMemberId(),
                board.getBoardname(),
                board.getDescription(),
                board.getNotice(),
                board.getCreatedAt(),
                board.getModifiedAt()
        );
    }
}
