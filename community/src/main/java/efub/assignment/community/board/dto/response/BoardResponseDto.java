package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;

import java.time.LocalDateTime;

public record BoardResponseDto (
        Long boardId,
        Long memberId,
        String boardOwnerName,
        String title,
        String description,
        String notice,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static BoardResponseDto from (Board board) {
        return new BoardResponseDto(
                board.getBoardId(),
                board.getBoardOwner().getMemberId(),
                board.getBoardOwner().getNickname(),
                board.getTitle(),
                board.getDescription(),
                board.getNotice(),
                board.getCreatedAt(),
                board.getModifiedAt()
        );
    }
}
