package efub.assignment.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Default
    INTERNAL_SERVER_ERROR(500, "예상치 못한 서버에러가 발생했습니다."),
    ERROR(400, "요청 처리에 실패했습니다."),

    // account
    ACCOUNT_NOT_FOUND(404, "존재하는 계정이 없습니다."),

    // post
    POST_NOT_FOUND(404, "해당 id의 게시물이 존재하지 않습니다."),
    POST_ACCOUNT_MISMATCH(401, "게시글 생성자가 아닙니다."),
    POST_LIKE_NOT_FOUND(404, "게시글 좋아요가 존재하지 않습니다."),
    POST_LIKE_ALREADY_EXISTS(400, "게시글 좋아요가 이미 존재합니다."),

    // board
    BOARD_NOT_FOUND(404, "해당 id의 게시판이 존재하지 않습니다."),
    BOARD_ACCOUNT_MISMATCH(401, "게시판 주인이 아닙니다."),

    // comment
    COMMENT_NOT_FOUND(404, "해당 id의 댓글이 존재하지 않습니다."),
    COMMENT_ACCOUNT_MISMATCH(401, "댓글 주인이 아닙니다."),

    // message 관련
    MESSAGE_ROOM_NOT_FOUND(404, "존재하는 쪽지방이 없습니다."),
    MESSAGE_ROOM_ALREADY_EXISTS(409, "이미 존재하는 쪽지방이 있습니다."),
    CANNOT_MESSAGE_SELF(400, "자기 자신에게 쪽지를 보낼 수 없습니다."),
    NOT_MESSAGE_ROOM_PARTICIPANT(401, "해당 쪽지방의 일원이 아닙니다.");


    private final int status;
    private final String message;
}
