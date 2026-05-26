package efub.assignment.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Default
    INTERNAL_SERVER_ERROR(500, "예상치 못한 서버 에러가 발생하였습니다."),
    ERROR(400, "요청 처리에 실패했습니다."),

    // member
    ACCOUNT_NOT_FOUND(404, "존재하지 않는 계정입니다."),

    // post
    POST_NOT_FOUND(404, "존재하지 않는 게시물입니다."),
    POST_ACCOUNT_MISMATCH(403, "게시글 생성자가 아닙니다."),

    // board
    BOARD_NOT_FOUND(404, "존재하지 않는 게시판입니다."),
    BOARD_ACCOUNT_MISMATCH(403, "게시판 생성자가 아닙니다."),

    // comment
    COMMENT_NOT_FOUND(404, "존재하지 않는 댓글입니다."),
    COMMENT_ACCOUNT_MISMATCH(403, "댓글 작성자가 아닙니다."),

    // postLike
    LIKE_ALREADY_EXISTS(400, "좋아요가 이미 존재합니다."),
    LIKE_NOT_FOUND(404, "좋아요가 존재하지 않습니다."),

    // messageRoom
    MESSAGEROOM_NOT_FOUND(404, "쪽지방이 존재하지 않습니다."),

    // message
    NOT_MESSAGEROOM_MEMBER(403, "쪽지방의 참여자가 아닙니다.");

    private final int status;
    private final String message;
}
