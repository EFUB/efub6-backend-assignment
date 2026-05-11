package efub.assignment.community.board.dto.response;

import efub.assignment.community.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BoardOwnerUpdateResponse {

    private Long boardId;
    private String boardName;
    private String description;
    private String notice;
    private Long memberId;          // 새 주인의 memberId
    private String ownerNickname;   // 새 주인의 닉네임
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardOwnerUpdateResponse from(Board board) {
        return BoardOwnerUpdateResponse.builder()
                .boardId(board.getBoardId())
                .boardName(board.getBoardName())
                .description(board.getDescription())
                .notice(board.getNotice())
                .memberId(board.getOwner().getMemberId())
                .ownerNickname(board.getOwner().getNickname())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}