package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BoardGetResponse {

    private Long boardId;
    private String boardName;
    private String description;
    private String notice;
    private Long memberId;
    private String ownerNickname;
    private Boolean canEdit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardGetResponse from(Board board, boolean canEdit) {
        return BoardGetResponse.builder()
                .boardId(board.getBoardId())
                .boardName(board.getBoardName())
                .description(board.getDescription())
                .notice(board.getNotice())
                .memberId(board.getOwner().getMemberId())
                .ownerNickname(board.getOwner().getNickname())
                .canEdit(canEdit)
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}