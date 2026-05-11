package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;

import java.time.LocalDateTime;

public record BoardResponseDto(
        Long boardId,
        Long memberId,
        String boardName,
        String description,
        String notice,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static BoardResponseDto from(Board board) {
        return new BoardResponseDto(
                board.getBoardId(),
                board.getMember().getMemberId(),
                board.getBoardName(),
                board.getDescription(),
                board.getNotice(),
                board.getCreatedAt(),
                board.getModifiedAt()
        );
    }
}