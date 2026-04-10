package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;

import java.time.LocalDateTime;

public record BoardResponse(
        Long boardId,
        String name,
        String description,
        String notification,
        String ownerNickname,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getNotification(),
                board.getWriter().getNickname(),
                board.getCreatedAt(),
                board.getModifiedAt()
        );
    }
}
