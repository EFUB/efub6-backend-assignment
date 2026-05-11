package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
public class BoardCreateResponse {

    private Long boardId;
    private String boardName;
    private String description;
    private String notice;
    private String ownerNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardCreateResponse from(Board board) {
        return BoardCreateResponse.builder()
                .boardId(board.getBoardId())
                .boardName(board.getBoardName())
                .description(board.getDescription())
                .notice(board.getNotice())
                .ownerNickname(board.getOwner().getNickname())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}